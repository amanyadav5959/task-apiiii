package com.yourorg.task_api.auth;

import com.yourorg.task_api.api.ApiConstants;
import com.yourorg.task_api.auth.dto.AuthResponse;
import com.yourorg.task_api.auth.dto.LoginRequest;
import com.yourorg.task_api.auth.dto.RegisterRequest;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication")
@RestController
@RequestMapping(ApiConstants.V1 + "/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/register")
	public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
		return authService.register(request);
	}

	@PostMapping("/login")
	public AuthResponse login(@Valid @RequestBody LoginRequest request) {
		return authService.login(request);
	}
}
