package com.yourorg.task_api.task;

import java.util.List;

import com.yourorg.task_api.api.ApiConstants;
import com.yourorg.task_api.task.dto.TaskCreateRequest;
import com.yourorg.task_api.task.dto.TaskResponse;
import com.yourorg.task_api.task.dto.TaskUpdateRequest;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Tasks")
@SecurityRequirement(name = "bearer-jwt")
@RestController
@RequestMapping(ApiConstants.V1 + "/tasks")
@CrossOrigin(origins = "http://localhost:5173")
public class TaskController {

	private final TaskService taskService;

	public TaskController(TaskService taskService) {
		this.taskService = taskService;
	}

	@GetMapping
	public List<TaskResponse> list() {
		return taskService.listForCurrentUser();
	}

	@GetMapping("/{id}")
	public TaskResponse get(@PathVariable Long id) {
		return taskService.get(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public TaskResponse create(@Valid @RequestBody TaskCreateRequest request) {
		return taskService.create(request);
	}

	@PutMapping("/{id}")
	public TaskResponse update(@PathVariable Long id, @Valid @RequestBody TaskUpdateRequest request) {
		return taskService.update(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		taskService.delete(id);
	}
}
