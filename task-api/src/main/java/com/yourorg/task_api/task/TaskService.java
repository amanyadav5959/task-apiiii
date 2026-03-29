package com.yourorg.task_api.task;

import java.time.Instant;
import java.util.List;

import com.yourorg.task_api.task.dto.TaskCreateRequest;
import com.yourorg.task_api.task.dto.TaskResponse;
import com.yourorg.task_api.task.dto.TaskUpdateRequest;
import com.yourorg.task_api.user.User;
import com.yourorg.task_api.user.UserRepository;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TaskService {

	private final TaskRepository taskRepository;
	private final UserRepository userRepository;

	public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
		this.taskRepository = taskRepository;
		this.userRepository = userRepository;
	}

	@Transactional(readOnly = true)
	public List<TaskResponse> listForCurrentUser() {
		if (isAdmin()) {
			return taskRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")).stream()
					.map(this::toResponse)
					.toList();
		}
		return taskRepository.findByUser_EmailOrderByCreatedAtDesc(currentEmail()).stream()
				.map(this::toResponse)
				.toList();
	}

	@Transactional(readOnly = true)
	public TaskResponse get(Long id) {
		Task task = loadTaskForCurrentUser(id);
		return toResponse(task);
	}

	@Transactional
	public TaskResponse create(TaskCreateRequest request) {
		User owner = userRepository.findByEmail(currentEmail())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
		Task task = new Task();
		task.setTitle(request.title().trim());
		task.setDescription(request.description() != null ? request.description().trim() : null);
		task.setStatus(request.status() != null ? request.status() : TaskStatus.TODO);
		task.setCreatedAt(Instant.now());
		task.setUser(owner);
		return toResponse(taskRepository.save(task));
	}

	@Transactional
	public TaskResponse update(Long id, TaskUpdateRequest request) {
		Task task = loadTaskForCurrentUser(id);
		task.setTitle(request.title().trim());
		task.setDescription(request.description() != null ? request.description().trim() : null);
		task.setStatus(request.status());
		return toResponse(taskRepository.save(task));
	}

	@Transactional
	public void delete(Long id) {
		Task task = loadTaskForCurrentUser(id);
		taskRepository.delete(task);
	}

	private Task loadTaskForCurrentUser(Long id) {
		if (isAdmin()) {
			return taskRepository.findById(id)
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
		}
		return taskRepository.findByIdAndUser_Email(id, currentEmail())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
	}

	private TaskResponse toResponse(Task task) {
		return new TaskResponse(
				task.getId(),
				task.getTitle(),
				task.getDescription(),
				task.getStatus(),
				task.getCreatedAt(),
				task.getUser().getEmail());
	}

	private String currentEmail() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated()) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
		}
		return auth.getName();
	}

	private boolean isAdmin() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null) {
			return false;
		}
		return auth.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.anyMatch("ROLE_ADMIN"::equals);
	}
}
