<template>
  <v-container class="pa-10" fluid>
    <!-- Кнопка для открытия диалога создания задачи -->
    <v-row justify="center">
      <v-col cols="12" sm="8" md="6">
        <h1 class="text-h4 text-center mb-8">Список задач</h1>
        <v-btn color="primary" block @click="openCreateDialog()" class="mb-8">
          Добавить задачу
        </v-btn>

        <!-- Список задач для каждого пользователя -->
        <template v-for="user in users" :key="user.id">
          <v-card v-if="tasksByUser[user.id] && tasksByUser[user.id].length > 0" elevation="2" class="mb-8">
            <v-card-title>Задачи для {{ user.username }}</v-card-title>
            <v-list>
              <v-list-item
                  v-for="task in tasksByUser[user.id]"
                  :key="task.id"
                  class="mb-2 px-4 py-2"
              >
                <v-row align="center" no-gutters>
                  <!-- Отображение названия задачи (только для чтения) -->
                  <v-col cols="12" sm="7" class="pr-2">
                    <div class="d-flex flex-column">
                      <div class="font-weight-bold mb-1">{{ task.name }}</div>
                      <div class="text-caption mb-1">{{ task.description || 'Без описания' }}</div>
                      <div class="text-caption">{{ formatDate(task.date) }}</div>
                    </div>
                  </v-col>

                  <!-- Кнопки управления -->
                  <v-col cols="12" sm="2" class="d-flex justify-center px-2">
                    <v-btn
                        color="success"
                        small
                        @click="openEditDialog(task)"
                        class="mb-2"
                    >
                      Редактировать
                    </v-btn>
                  </v-col>

                  <v-col cols="12" sm="2" class="d-flex justify-center px-2">
                    <v-btn
                        color="error"
                        small
                        @click="deleteTask(task.id)"
                        class="mb-2"
                    >
                      Удалить
                    </v-btn>
                  </v-col>
                </v-row>
              </v-list-item>
            </v-list>
          </v-card>

          <!-- Сообщение, если задач нет для пользователя -->
          <v-alert v-else type="info" class="mt-4 text-center">
            Нет задач для {{ user.username }}.
          </v-alert>
        </template>
      </v-col>
    </v-row>

    <!-- Всплывающее окно для создания/редактирования задачи -->
    <v-dialog v-model="openDialog" max-width="500">
      <v-card>
        <v-card-title>{{ dialogTitle }}</v-card-title>
        <v-card-text>
          <v-select
              v-model="currentTask.userId"
              :items="users"
              item-title="username"
              item-value="id"
              label="Выберите пользователя"
              variant="outlined"
              class="mb-4"
          ></v-select>
          <v-text-field
              v-model="currentTask.name"
              label="Название задачи"
              variant="outlined"
              clearable
              hide-details
              class="mb-4"
          ></v-text-field>
          <v-textarea
              v-model="currentTask.description"
              label="Описание задачи"
              variant="outlined"
              rows="3"
              hide-details
              class="mb-4"
          ></v-textarea>
          <v-text-field
              v-model="currentTask.date"
              label="Дата выполнения"
              type="date"
              variant="outlined"
              hide-details
              class="mb-4"
          ></v-text-field>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn text @click="closeDialog">Отмена</v-btn>
          <v-btn color="primary" @click="saveTask" :disabled="!currentTask.name.trim()">
            {{ isEditing ? 'Обновить' : 'Добавить' }}
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script>
export default {
  data() {
    return {
      users: [], // Массив пользователей
      tasks: [], // Массив всех задач
      currentTask: {
        id: null,
        userId: null,
        name: '',
        description: '',
        date: '',
      },
      openDialog: false, // Флаг для отображения диалогового окна
      isEditing: false, // Флаг для режима редактирования
      snackbar: {
        show: false,
        text: '',
        color: '',
      },
    };
  },
  computed: {
    // Группировка задач по пользователям
    tasksByUser() {
      return this.users.reduce((acc, user) => {
        acc[user.id] = this.tasks.filter((task) => task.user.id === user.id);
        return acc;
      }, {});
    },

    // Заголовок диалогового окна
    dialogTitle() {
      return this.isEditing ? 'Редактирование задачи' : 'Добавление новой задачи';
    },
  },
  methods: {
    /**
     * Получение списка пользователей и задач с сервера
     */
    async fetchTasksAndUsers() {
      try {
        const [usersResponse, tasksResponse] = await Promise.all([
          this.$axios.get('/users'), // Получаем список пользователей
          this.$axios.get('/tasks'), // Получаем все задачи
        ]);

        this.users = usersResponse.data || []; // Если данных нет, используем пустой массив

        this.tasks = (tasksResponse.data || []).map((task) => ({
          ...task,
          date: task.date ? new Date(task.date).toISOString().split('T')[0] : '', // Преобразование даты
        }));

        // Проверяем, что каждый task имеет связанного пользователя
        this.tasks = this.tasks.filter((task) => task.user !== undefined);
      } catch (error) {
        console.error('Ошибка получения данных:', error);
        this.showSnackbar('Ошибка загрузки данных', 'error');
      }
    },
    /**
     * Открытие диалога для создания новой задачи
     */
    openCreateDialog() {
      this.isEditing = false;
      this.resetCurrentTask();
      this.openDialog = true;
    },

    /**
     * Открытие диалога для редактирования задачи
     */
    openEditDialog(task) {
      this.isEditing = true;
      this.currentTask = { ...task }; // Создаем копию задачи
      this.openDialog = true;
    },

    /**
     * Сохранение задачи (создание или обновление)
     */
    async saveTask() {
      if (!this.currentTask.name.trim()) return;

      try {
        if (this.isEditing) {
          // Обновление существующей задачи
          await this.$axios.put(`/tasks/${this.currentTask.id}`, {
            name: this.currentTask.name,
            description: this.currentTask.description,
            date: this.currentTask.date,
            user_id: this.currentTask.userId,
          });
          const updatedTask = { ...this.currentTask };
          updatedTask.date = updatedTask.date.split('T')[0]; // Преобразование даты
          this.updateTaskInList(updatedTask);
          this.showSnackbar('Задача успешно обновлена', 'success');
        } else {
          // Создание новой задачи
          const response = await this.$axios.post('/tasks', {
            name: this.currentTask.name,
            description: this.currentTask.description,
            date: this.currentTask.date || new Date().toISOString().split('T')[0], // Текущая дата, если не указана
            user_id: this.currentTask.userId,
          });

          const newTask = response.data;
          newTask.date = newTask.date.split('T')[0]; // Преобразование даты
          newTask.user = this.users.find((user) => user.id === newTask.user.id); // Привязка пользователя
          this.tasks.push(newTask);

          this.showSnackbar('Задача успешно добавлена', 'success');
        }

        this.closeDialog();
      } catch (error) {
        console.error('Ошибка сохранения задачи:', error);
        this.showSnackbar('Ошибка сохранения задачи', 'error');
      }
    },

    /**
     * Обновление задачи в списке
     */
    updateTaskInList(updatedTask) {
      const index = this.tasks.findIndex((task) => task.id === updatedTask.id);
      if (index !== -1) {
        this.$set(this.tasks, index, updatedTask); // Обновляем задачу в списке
      }
    },

    /**
     * Закрытие диалогового окна
     */
    closeDialog() {
      this.openDialog = false;
      this.resetCurrentTask();
    },

    /**
     * Сброс текущей задачи
     */
    resetCurrentTask() {
      this.currentTask = {
        id: null,
        userId: null,
        name: '',
        description: '',
        date: '',
      };
    },

    /**
     * Форматирование даты
     */
    formatDate(date) {
      return date ? new Date(date).toLocaleDateString() : '';
    },

    /**
     * Удаление задачи
     */
    async deleteTask(id) {
      try {
        await this.$axios.delete(`/tasks/${id}`);
        this.tasks = this.tasks.filter((task) => task.id !== id);
        this.showSnackbar('Задача успешно удалена', 'success');
      } catch (error) {
        console.error('Ошибка удаления задачи:', error);
        this.showSnackbar('Ошибка удаления задачи', 'error');
      }
    },

    /**
     * Показать уведомление
     */
    showSnackbar(text, color) {
      this.snackbar.text = text;
      this.snackbar.color = color;
      this.snackbar.show = true;
    },
  },
  mounted() {
    this.fetchTasksAndUsers(); // Загрузка данных при загрузке компонента
  },
};
</script>

<style scoped>
/* Стили для компонента */
.v-container {
  max-width: 800px;
  margin: 0 auto;
}

.v-card {
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.v-btn {
  text-transform: none; /* Отключаем преобразование текста */
}

.mb-4 {
  margin-bottom: 16px !important;
}

.px-2 {
  padding-left: 8px !important;
  padding-right: 8px !important;
}

.pr-2 {
  padding-right: 8px !important;
}

.py-2 {
  padding-top: 8px !important;
  padding-bottom: 8px !important;
}
</style>