package com.malkollm.jointtasks.repository;

import com.malkollm.jointtasks.model.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUser_Id(Long userId); // Поиск задач по ID пользователя
}