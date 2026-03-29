package com.yourorg.task_api.task;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

	List<Task> findByUser_EmailOrderByCreatedAtDesc(String email);

	Optional<Task> findByIdAndUser_Email(Long id, String email);
}
