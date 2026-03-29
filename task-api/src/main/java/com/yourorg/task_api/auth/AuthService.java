package com.yourorg.task_api.auth;

import com.yourorg.task_api.auth.dto.AuthResponse;
import com.yourorg.task_api.auth.dto.LoginRequest;
import com.yourorg.task_api.auth.dto.RegisterRequest;
import com.yourorg.task_api.security.JwtService;
import com.yourorg.task_api.user.Role;
import com.yourorg.task_api.user.User;
import com.yourorg.task_api.user.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
	}

	@Transactional
	public AuthResponse register(RegisterRequest request) {
		String email = request.email().trim().toLowerCase();
		if (userRepository.existsByEmail(email)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
		}
		User user = new User();
		user.setEmail(email);
		user.setPassword(passwordEncoder.encode(request.password()));
		user.setRole(Role.USER);
		userRepository.save(user);
		String token = jwtService.generateToken(user);
		return AuthResponse.of(token, user.getEmail(), user.getRole());
	}

	public AuthResponse login(LoginRequest request) {
		String email = request.email().trim().toLowerCase();
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new BadCredentialsException("Invalid email or password"));
		if (!passwordEncoder.matches(request.password(), user.getPassword())) {
			throw new BadCredentialsException("Invalid email or password");
		}
		String token = jwtService.generateToken(user);
		return AuthResponse.of(token, user.getEmail(), user.getRole());
	}
}
