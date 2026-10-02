package io.github.thawansilva.payment_service.modules.users.internal;

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
@Table(name = "refresh_tokens")
public class RefreshToken {
	@Id 
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	
	@Column(name = "user_id", nullable = false)
	private UUID userId;

	@Column(unique = true, nullable = false)
	private String token;

	@Column(nullable = false)
	private boolean revoked = false;

	@Column(name = "expires_at", nullable = false)
	private Instant expiresAt;

	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }

	protected RefreshToken() {}

	public RefreshToken(String token, UUID userId, Instant expiresAt) {
		this.token = token;
		this.userId = userId;
		this.expiresAt = expiresAt;
	}

	public boolean isValidToken() {
		return (!isRevoked() && !isExpired());
	}

	public boolean isExpired() { return (Instant.now().isAfter(expiresAt)); }

	public Instant getCreatedAt() { return createdAt; }

	public Instant getExpiresAt() { return expiresAt; }

	public UUID getId() { return id; }

	public String getToken() { return token; }

	public UUID getUserId() { return userId; }
	
	public boolean isRevoked() { return revoked; }
}
