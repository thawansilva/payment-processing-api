package io.github.thawansilva.payment_service.modules.users.internal;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
	Optional<RefreshToken> findByToken(String token);

	@Modifying
	@Query("UPDATE RefreshToken r SET r.revoked = true WHERE r.userId = :userId")
	void revokeAllByUserId(UUID userId);
}