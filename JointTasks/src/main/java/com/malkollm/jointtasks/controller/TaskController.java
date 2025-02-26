package com.malkollm.jointtasks.controller;

import com.malkollm.jointtasks.model.dto.TaskDTO;
import com.malkollm.jointtasks.model.entity.Task;
import com.malkollm.jointtasks.model.entity.User;
import com.malkollm.jointtasks.service.TaskService;
import com.malkollm.jointtasks.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;
    @Autowired
    private UserService userService;

    // Получение всех задач
    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    // Получение задач для конкретного пользователя
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Task>> getTasksByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(taskService.getTasksByUserId(userId));
    }

    // Создание новой задачи
    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@RequestBody Task task) {
        // Находим пользователя по userId
        User optionalUser = userService.getUserById(task.getUserId());


        // Устанавливаем ссылку на пользователя
        task.setUser(optionalUser);

        // Если дата не указана, устанавливаем текущую дату
        if (task.getDate() == null) {
            task.setDate(new java.sql.Timestamp(System.currentTimeMillis()));
        }

        // Создаем задачу
        Task createdTask = taskService.createTask(task);

        // Возвращаем созданную задачу в формате DTO
        return ResponseEntity.ok(new TaskDTO(createdTask));
    }

    // Обновление существующей задачи
    @PutMapping("/{taskId}")
    public ResponseEntity<Task> updateTask(@PathVariable Long taskId, @RequestBody Task updatedTask) {
        return ResponseEntity.ok(taskService.updateTask(taskId, updatedTask));
    }

    // Удаление задачи
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }
}