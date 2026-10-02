package io.github.thawansilva.payment_service.modules.transactions.internal;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "idempotency_keys")
public class IdempotencyKey {
	@Id
	@Column(name = "idempotency_key")
	private String key;

	@Column(name = "transaction_id", nullable = false)
	private UUID transactionId;

	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	@PrePersist
	protected void onCreate() {
		createdAt = Instant.now();
	}

	public IdempotencyKey() {
	}

	public IdempotencyKey(String key, UUID transactionId) {
		this.key = key;
		this.transactionId = transactionId;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public String getKey() {
		return key;
	}

	public UUID getTransactionId() {
		return transactionId;
	}
}
