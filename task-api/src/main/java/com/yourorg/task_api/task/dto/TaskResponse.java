package com.yourorg.task_api.task.dto;

import java.time.Instant;

import com.yourorg.task_api.task.TaskStatus;

public record TaskResponse(
		Long id,
		String title,
		String description,
		TaskStatus status,
		Instant createdAt,
		String ownerEmail
) {
}
