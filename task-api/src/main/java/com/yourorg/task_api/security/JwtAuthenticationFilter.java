package com.yourorg.task_api.security;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	private final UserDetailsService userDetailsService;

	public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
		this.jwtService = jwtService;
		this.userDetailsService = userDetailsService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		String token = extractJwtFromAuthorizationHeader(authHeader);
		if (token == null || token.isEmpty()) {
			filterChain.doFilter(request, response);
			return;
		}
		try {
			String email = jwtService.extractEmail(token);
			if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				UserDetails userDetails = userDetailsService.loadUserByUsername(email);
				if (jwtService.isTokenValid(token, userDetails)) {
					UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities());
					auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(auth);
				}
			}
		}
		catch (Exception ignored) {
			// Invalid token: leave unauthenticated; secured endpoints will return 401
		}
		filterChain.doFilter(request, response);
	}

	/**
	 * Accepts {@code Authorization: Bearer <jwt>} (RFC 6750) or a raw JWT in the header
	 * (three dot-separated segments) for easier local testing with HTTP clients.
	 */
	private static String extractJwtFromAuthorizationHeader(String authHeader) {
		if (authHeader == null || authHeader.isBlank()) {
			return null;
		}
		String trimmed = authHeader.trim();
		if (trimmed.regionMatches(true, 0, "Bearer ", 0, 7)) {
			return trimmed.substring(7).trim();
		}
		String[] parts = trimmed.split("\\.");
		if (parts.length == 3) {
			return trimmed;
		}
		return null;
	}
}
