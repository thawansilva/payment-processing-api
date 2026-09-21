package io.github.thawansilva.payment_service.modules.users.api;

import jakarta.validation.constraints.NotEmpty;

public record RefreshRequestDto(@NotEmpty String refreshToken) { }
