package io.github.thawansilva.payment_service.modules.users.internal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record  RequestRegisterDto (
	@NotBlank @Size(min=3, max=50) String name,
	@NotBlank @Size(min=8) String password
) {}
