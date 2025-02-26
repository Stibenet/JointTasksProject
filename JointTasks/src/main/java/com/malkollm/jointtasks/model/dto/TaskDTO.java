package com.malkollm.jointtasks.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.malkollm.jointtasks.model.entity.Task;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // Исключаем null-значения
public class TaskDTO {

    private Long id;
    private String name;
    private String description;
    private LocalDateTime date;
    private LocalDateTime createdAt;
    private UserDTO user;

    public TaskDTO(Task task) {
        this.id = task.getId();
        this.name = task.getName();
        this.description = task.getDescription();
        this.date = task.getDate() != null ? task.getDate().toLocalDateTime() : null;
        this.createdAt = task.getCreatedAt() != null ? task.getCreatedAt().toLocalDateTime() : null;

        if (task.getUser() != null) {
            this.user = new UserDTO(task.getUser());
        }
    }
}