package com.malkollm.jointtasks.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.malkollm.jointtasks.model.entity.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // Исключаем null-значения
public class UserDTO {

    private Long id;
    private String username;
    private LocalDateTime createdAt;

    public UserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.createdAt = user.getCreatedAt().toLocalDateTime();
    }
}
