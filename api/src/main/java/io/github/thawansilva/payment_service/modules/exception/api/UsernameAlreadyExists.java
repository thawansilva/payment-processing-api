package io.github.thawansilva.payment_service.modules.exception.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UsernameAlreadyExists extends RuntimeException {

	public UsernameAlreadyExists(String name) {
		super("Username already exists: " + name);
	}
}
