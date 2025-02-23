import axios from 'axios';

const apiClient = axios.create({
    baseURL: 'http://localhost:8080/api', // Замените на URL вашего сервера
    headers: {
        'Content-Type': 'application/json',
    },
});

export default apiClient;