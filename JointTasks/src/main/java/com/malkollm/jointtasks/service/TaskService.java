package com.malkollm.jointtasks.service;

import com.malkollm.jointtasks.model.dto.TaskDTO;
import com.malkollm.jointtasks.model.entity.Task;
import com.malkollm.jointtasks.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    // Получение всех задач
    public List<TaskDTO> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(TaskDTO::new) // Преобразуем Task в TaskDTO
                .collect(Collectors.toList());
    }

    // Получение задач для конкретного пользователя
    public List<Task> getTasksByUserId(Long userId) {
        return taskRepository.findByUser_Id(userId);
    }

    // Создание новой задачи
    public Task createTask(Task task) {
        if (task.getUser() == null) {
            throw new IllegalArgumentException("Пользователь не связан с задачей");
        }
        return taskRepository.save(task);
    }

    // Обновление существующей задачи
    public Task updateTask(Long taskId, Task updatedTask) {
        return taskRepository.findById(taskId).map(task -> {
            task.setName(updatedTask.getName());
            task.setDescription(updatedTask.getDescription());
            task.setDate(updatedTask.getDate());
            return taskRepository.save(task);
        }).orElseThrow(() -> new RuntimeException("Task not found"));
    }

    // Удаление задачи
    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }
}