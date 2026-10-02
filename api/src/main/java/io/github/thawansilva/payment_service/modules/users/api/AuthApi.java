package io.github.thawansilva.payment_service.modules.users.api;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.thawansilva.payment_service.modules.users.internal.AuthService;

@Component
public class AuthApi {
	@Autowired
	final private AuthService authService;

	public AuthApi(AuthService authService) {
		this.authService = authService;
	}

	public UUID getIdByUsername(String username) {
		return authService.getIdByUsername(username);
	}
}
