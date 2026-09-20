package io.github.thawansilva.payment_service.modules.users.api;

import jakarta.validation.constraints.NotEmpty;

public record LoginRequestDto(
	@NotEmpty String username,
	@NotEmpty String password) { }