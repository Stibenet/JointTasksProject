<template>
  <v-container class="pa-10" fluid>
    <v-row justify="center">
      <v-col cols="12" sm="8" md="6">
        <!-- Заголовок -->
        <h1 class="text-h4 text-center mb-8">Список задач</h1>

        <!-- Форма добавления задачи -->
        <v-card class="mb-8 pa-4" elevation="2">
          <v-text-field
              v-model="newTaskName"
              label="Название задачи"
              variant="outlined"
              clearable
              @keydown.enter="addTask"
              class="mb-4"
          ></v-text-field>
          <v-btn
              block
              color="primary"
              @click="addTask"
              :disabled="!newTaskName.trim()"
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
                  ></v-text-field>
                </v-col>

                <!-- Кнопка сохранения -->
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

                <!-- Кнопка удаления -->
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
      newTaskName: '', // Новое название задачи
    };
  },
  methods: {
    /**
     * Получение списка задач с сервера
     */
    async fetchTasks() {
      try {
        const response = await this.$axios.get('/tasks'); // Замените URL на ваш API
        this.tasks = response.data;
      } catch (error) {
        console.error('Ошибка получения задач:', error);
      }
    },

    /**
     * Добавление новой задачи
     */
    async addTask() {
      if (!this.newTaskName.trim()) return;

      try {
        const response = await this.$axios.post('/tasks', {
          name: this.newTaskName,
        });
        this.tasks.push(response.data); // Добавляем новую задачу в список
        this.newTaskName = ''; // Очищаем поле ввода
      } catch (error) {
        console.error('Ошибка добавления задачи:', error);
      }
    },

    /**
     * Обновление задачи
     */
    async updateTask(task) {
      try {
        await this.$axios.put(`/tasks/${task.id}`, task);
        console.log('Задача обновлена:', task);
      } catch (error) {
        console.error('Ошибка обновления задачи:', error);
      }
    },

    /**
     * Удаление задачи
     */
    async deleteTask(id) {
      try {
        await this.$axios.delete(`/tasks/${id}`);
        this.tasks = this.tasks.filter((task) => task.id !== id); // Удаляем задачу из списка
      } catch (error) {
        console.error('Ошибка удаления задачи:', error);
      }
    },
  },
  mounted() {
    this.fetchTasks(); // Загрузка задач при загрузке компонента
  },
};
</script>

<style scoped>
/* Центрирование содержимого */
.v-container {
  max-width: 800px;
  margin: 0 auto;
}

/* Карточки */
.v-card {
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

/* Кнопки */
.v-btn {
  text-transform: none; /* Отключаем преобразование текста */
}

/* Отступы между элементами */
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

/* Выравнивание текста в alert */
.v-alert {
  word-break: break-word;
}
</style>