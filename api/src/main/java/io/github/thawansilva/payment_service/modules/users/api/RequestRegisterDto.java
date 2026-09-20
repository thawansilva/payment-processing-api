package io.github.thawansilva.payment_service.modules.users.api;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record  RequestRegisterDto (
	@NotEmpty @Size(min=3, max=50) String username,
	@NotEmpty @Size(min=8) String password
) {}
