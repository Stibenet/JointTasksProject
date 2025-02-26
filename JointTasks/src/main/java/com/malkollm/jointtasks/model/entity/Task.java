package com.malkollm.jointtasks.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false) // Указываем имя столбца
    private String name;

    @Column(name = "description") // Опционально
    private String description;

    @Column(name = "date", nullable = false) // Указываем имя столбца
    private Timestamp date;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
        if (this.date == null) {
            this.date = new Timestamp(System.currentTimeMillis());
        }
    }
}