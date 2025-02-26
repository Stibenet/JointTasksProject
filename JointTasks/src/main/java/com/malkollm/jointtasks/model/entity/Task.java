package com.malkollm.jointtasks.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String description;

    @Column(nullable = false)
    private Timestamp date;

    @Column(nullable = false, updatable = false)
    private Timestamp createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    // Поле для передачи user_id через JSON
    @Setter
    @Getter
    @Transient
    private Long userId;

    // Конструктор без параметров
    public Task() {}

    // Геттеры и сеттеры для userId
//    public Long getUserId() {
//        return user != null ? user.getId() : null;
//    }

    @PrePersist
    public void prePersist() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
        if (this.date == null) {
            this.date = new Timestamp(System.currentTimeMillis());
        }
    }
}