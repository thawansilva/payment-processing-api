package io.github.thawansilva.payment_service.modules.users.internal;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service 
public class RefreshTokenService {
	@Autowired
	private final RefreshTokenRepository refreshTokenRepo;
	@Autowired 
	private final JwtService jwtService;

	public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, JwtService jwtService) {
		this.refreshTokenRepo = refreshTokenRepository;
		this.jwtService = jwtService;
	}

	@Transactional 
	public RefreshToken generateRefreshToken(UUID userId) {
		String refToken = jwtService.generateRefreshToken();
		Instant expiresAt = Instant
			.now()
			.plusMillis(jwtService.getRefreshTokenExpirationInMs());
		return refreshTokenRepo.save(new RefreshToken(refToken, userId, expiresAt));
	}

	@Transactional 
	public RefreshToken verifyAndGetToken(String token) {
		RefreshToken refToken = refreshTokenRepo
			.findByToken(token)
			.orElseThrow(() -> new IllegalArgumentException("Refresh token not found"));
		if (!refToken.isValidToken()) {
			refreshTokenRepo.delete(refToken);
			throw new IllegalArgumentException(
				refToken.isExpired()
			 	? "Refresh token expired" : "Refresh token revoked");
		}
		return refToken;
	}

	@Transactional 
	public void revokeAllByUserId(UUID userId) {
		refreshTokenRepo.revokeAllByUserId(userId);
	}

	@Transactional 
	public void revokeById(UUID id) {
		refreshTokenRepo.revokeById(id);
	}
}
