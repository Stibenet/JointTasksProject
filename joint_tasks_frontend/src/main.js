import { createApp } from 'vue';
import App from './App.vue';
import vuetify from './plugins/vuetify';
import apiClient from './plugins/axios';

const app = createApp(App);

// Регистрация $axios как глобального свойства
app.config.globalProperties.$axios = apiClient;

app.use(vuetify);
app.mount('#app');