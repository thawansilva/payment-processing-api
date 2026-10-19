package io.github.thawansilva.payment_service.modules.exception.api;

import javax.naming.AuthenticationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalHandlerException {

	@ExceptionHandler(UsernameAlreadyExists.class)
	public ResponseEntity<String> handleUsernameAlreadyExists(UsernameAlreadyExists e) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
	}

	@ExceptionHandler(UserNotFound.class)
	public ResponseEntity<String> handleUserNotFound(UserNotFound e) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid credentials");
	}

	@ExceptionHandler(TransactionNotFound.class)
	public ResponseEntity<String> handleTransactionNotFound(TransactionNotFound e) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid credentials");
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<String> handleBadCredentials(BadCredentialsException e) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid credentials");
	}

	@ExceptionHandler(DisabledException.class)
	public ResponseEntity<String> handleDisabledUser(DisabledException e) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
	}

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<String> handleAuthentication(AuthenticationException e) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
	}
}
