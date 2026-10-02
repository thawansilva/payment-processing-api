package io.github.thawansilva.payment_service.modules.transactions.internal;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionResponse(
		UUID id,
		UUID userId,
		BigDecimal amount,
		String currency,
		TransactionStatus status,
		Instant createdAt) {

	public static TransactionResponse from(Transaction transaction) {
		return new TransactionResponse(
				transaction.getId(),
				transaction.getAccountId(),
				transaction.getAmount(),
				transaction.getCurrency(),
				transaction.getTransactionStatus(),
				transaction.getCreatedAt());
	}
}
