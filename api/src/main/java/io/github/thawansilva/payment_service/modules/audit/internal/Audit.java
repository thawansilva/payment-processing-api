package io.github.thawansilva.payment_service.modules.audit.internal;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "audit_log")
public class Audit {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(name = "actor_id", nullable = false)
	private UUID actorId;

	@Column(name = "transaction_id", nullable = false)
	private UUID transactionId;

	@Column(nullable = false, length = 50)
	private String action;

	@Column(nullable = false, length = 500)
	private String details;

	@Column(name = "created_at")
	private Instant createdAt;

	@PrePersist
	protected void onCreate() { createdAt = Instant.now(); }

	protected Audit() { }

	public Audit(UUID actorId, UUID transactionId, String action,
		String details)
	{
		this.actorId = actorId;
		this.transactionId = transactionId;
		this.action = action;
		this.details = details;
	}

	public String getAction() { return action; }

	public UUID getId() { return id; }

	public UUID getActorId() { return actorId; }

	public String getDetails() { return details; }

	public UUID getTransactionId() { return transactionId; }

	public Instant getCreatedAt() { return createdAt; }
}
