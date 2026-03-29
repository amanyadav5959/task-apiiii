package com.yourorg.task_api.api.v1;

import java.util.Map;

import com.yourorg.task_api.api.ApiConstants;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Health")
@RestController
@RequestMapping(ApiConstants.V1)
public class HealthController {

	@GetMapping("/health")
	public Map<String, String> health() {
		return Map.of(
				"status", "UP",
				"apiVersion", "v1");
	}
}
