package io.github.thawansilva.payment_service.modules.users.internal;

import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service 
public class JwtService {

	@Value("${spring.jwt.secret-key}")
	private String secretKey;

	@Value("${spring.jwt.expiration-ms}")
	private long accessTokenExpirationInMs;
	
	@Value("${spring.jwt.refresh-expiration-ms}")
	private long refreshTokenExpirationInMs;

	private boolean isExpiredToken(String token) {
		return extractClaims(token, Claims::getExpiration).before(new Date());
	}

	private <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
		Claims claims = Jwts
			.parser()
			.verifyWith(getSigningKey())
			.build()
			.parseSignedClaims(token)
			.getPayload();
		return claimsResolver.apply(claims);
	}

	private SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(secretKey.getBytes());
	}

	public String extractUsername(String token) {
		return extractClaims(token, Claims::getSubject);
	}

	public String generateToken(UserDetails userDetails) {
		SecretKey key = getSigningKey();
		Date now = new Date();
		Date expDate = new Date(now.getTime() + accessTokenExpirationInMs);

		return Jwts
			.builder()
			.subject(userDetails.getUsername())
			.claim("roles", userDetails.getAuthorities()
				.stream()
				.map(Object::toString)
				.toList())
			.issuedAt(now)
			.expiration(expDate)
			.signWith(key)
			.compact();
	}

	public boolean isValidToken(String token, UserDetails user) {
		String username = extractClaims(token, Claims::getSubject);
		return username.equals(user.getUsername()) && isExpiredToken(token);
	}

    public String generateRefreshToken() {
        return UUID.randomUUID().toString();
    }

    public long getAccessTokenExpirationInMs() {
        return accessTokenExpirationInMs;
    }
	
	public long getRefreshTokenExpirationInMs() {
		return refreshTokenExpirationInMs;
	}
}
