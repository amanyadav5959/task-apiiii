package com.yourorg.task_api.auth.dto;

import com.yourorg.task_api.user.Role;

public record AuthResponse(
		String accessToken,
		String tokenType,
		String email,
		Role role
) {
	public static AuthResponse of(String accessToken, String email, Role role) {
		return new AuthResponse(accessToken, "Bearer", email, role);
	}
}
