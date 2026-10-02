package io.github.thawansilva.payment_service.modules.transactions.api;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record CreateTransactionRequestDto(@NotNull
	@DecimalMin(value = "0.01", message = "Amount must be positive")
	BigDecimal amount,
	@NotNull(message = "Currency is required") String currency) { }
