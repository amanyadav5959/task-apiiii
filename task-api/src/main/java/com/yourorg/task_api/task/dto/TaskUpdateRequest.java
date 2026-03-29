package com.yourorg.task_api.task.dto;

import com.yourorg.task_api.task.TaskStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TaskUpdateRequest(
		@NotBlank @Size(max = 200) String title,
		@Size(max = 2000) String description,
		@NotNull TaskStatus status
) {
}
