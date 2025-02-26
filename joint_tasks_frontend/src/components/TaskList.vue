<template>
  <v-container class="pa-10" fluid>
    <!-- Уведомление -->
    <v-snackbar
        v-model="snackbar.show"
        :color="snackbar.color"
        location="top"
        timeout="3000"
    >
      {{ snackbar.text }}
    </v-snackbar>

    <v-row justify="center">
      <v-col cols="12" sm="8" md="6">
        <h1 class="text-h4 text-center mb-8">Список задач</h1>

        <!-- Форма добавления задачи -->
        <v-card class="mb-8 pa-4" elevation="2">
          <v-text-field
              v-model="newTask.name"
              label="Название задачи"
              variant="outlined"
              clearable
              hide-details
              @keydown.enter="addTask"
              class="mb-4"
          ></v-text-field>
          <v-textarea
              v-model="newTask.description"
              label="Описание задачи"
              variant="outlined"
              clearable
              hide-details
              rows="3"
              class="mb-4"
          ></v-textarea>
          <v-text-field
              v-model="newTask.date"
              label="Дата выполнения"
              type="date"
              variant="outlined"
              clearable
              hide-details
              class="mb-4"
          ></v-text-field>
          <v-btn
              block
              color="primary"
              @click="addTask"
              :disabled="!newTask.name.trim()"
              class="mb-4"
          >
            Добавить задачу
          </v-btn>
        </v-card>

        <!-- Список задач -->
        <v-card v-if="tasks.length > 0" elevation="2" class="mb-8">
          <v-list>
            <v-list-item
                v-for="task in tasks"
                :key="task.id"
                class="mb-2 px-4 py-2"
            >
              <v-row align="center" no-gutters>
                <!-- Редактирование названия задачи -->
                <v-col cols="12" sm="7" class="pr-2">
                  <v-text-field
                      v-model="task.name"
                      label="Название"
                      variant="outlined"
                      dense
                      hide-details
                      class="mb-2"
                  >
                    <template #append-inner>
                      <!-- Оставьте пустым -->
                    </template>
                  </v-text-field>
                  <v-textarea
                      v-model="task.description"
                      label="Описание"
                      variant="outlined"
                      dense
                      rows="2"
                      hide-details
                      class="mb-2"
                  ></v-textarea>
                  <v-text-field
                      v-model="task.date"
                      label="Дата выполнения"
                      type="date"
                      variant="outlined"
                      dense
                      hide-details
                      class="mb-2"
                  ></v-text-field>
                </v-col>

                <!-- Кнопки управления -->
                <v-col cols="12" sm="2" class="d-flex justify-center px-2">
                  <v-btn
                      color="success"
                      small
                      @click="updateTask(task)"
                      :disabled="!task.name.trim()"
                      class="mb-2"
                  >
                    Сохранить
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

        <!-- Сообщение, если задач нет -->
        <v-alert v-else type="info" class="mt-8 text-center">
          Список задач пуст.
        </v-alert>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
export default {
  data() {
    return {
      tasks: [], // Массив задач
      newTask: {
        name: '',
        description: '',
        date: '', // Дата в формате YYYY-MM-DD
      },
      snackbar: {
        show: false,
        text: '',
        color: '',
      },
    };
  },
  methods: {
    /**
     * Получение списка задач с сервера
     */
    async fetchTasks() {
      try {
        const response = await this.$axios.get('/tasks');
        this.tasks = response.data.map((task) => ({
          ...task,
          date: task.date ? new Date(task.date).toISOString().split('T')[0] : '', // Преобразование даты
        }));
      } catch (error) {
        console.error('Ошибка получения задач:', error);
        this.showSnackbar('Ошибка загрузки задач', 'error');
      }
    },

    /**
     * Добавление новой задачи
     */
    async addTask() {
      if (!this.newTask.name.trim()) return;

      try {
        const response = await this.$axios.post('/tasks', {
          name: this.newTask.name,
          description: this.newTask.description,
          date: this.newTask.date || new Date().toISOString().split('T')[0], // Текущая дата, если не указана
        });
        const newTask = response.data;
        newTask.date = newTask.date.split('T')[0]; // Преобразование даты
        this.tasks.push(newTask);
        this.resetNewTask();
        this.showSnackbar('Задача успешно добавлена', 'success');
      } catch (error) {
        console.error('Ошибка добавления задачи:', error);
        this.showSnackbar('Ошибка добавления задачи', 'error');
      }
    },

    /**
     * Обновление задачи
     */
    async updateTask(task) {
      try {
        await this.$axios.put(`/tasks/${task.id}`, {
          name: task.name,
          description: task.description,
          date: task.date,
        });
        this.showSnackbar('Задача успешно обновлена', 'success');
      } catch (error) {
        console.error('Ошибка обновления задачи:', error);
        this.showSnackbar('Ошибка обновления задачи', 'error');
      }
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
     * Сброс формы создания задачи
     */
    resetNewTask() {
      this.newTask = {
        name: '',
        description: '',
        date: '',
      };
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
    this.fetchTasks(); // Загрузка задач при загрузке компонента
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