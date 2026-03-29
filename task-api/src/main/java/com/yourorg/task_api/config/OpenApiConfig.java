package com.yourorg.task_api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI taskApiOpenAPI() {
		final String schemeName = "bearer-jwt";
		return new OpenAPI()
				.info(new Info()
						.title("Task API")
						.description("REST API v1 — auth (register/login), tasks. Use **Authorize** and paste a JWT from `POST /api/v1/auth/login` (value only, without `Bearer ` prefix is accepted by Swagger UI when using HTTP bearer scheme).")
						.version("v1"))
				.components(new Components()
						.addSecuritySchemes(schemeName,
								new SecurityScheme()
										.name(schemeName)
										.type(SecurityScheme.Type.HTTP)
										.scheme("bearer")
										.bearerFormat("JWT")
										.description("JWT returned by `/api/v1/auth/login` or `/api/v1/auth/register`")));
	}
}
