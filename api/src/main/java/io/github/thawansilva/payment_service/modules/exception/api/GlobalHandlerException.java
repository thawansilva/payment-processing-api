package io.github.thawansilva.payment_service.modules.exception.api;

import javax.naming.AuthenticationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.github.thawansilva.payment_service.modules.exception.internal.BodyErrorResponse;

@RestControllerAdvice
public class GlobalHandlerException {

	@ExceptionHandler(UsernameAlreadyExists.class)
	public ResponseEntity<BodyErrorResponse> handleUsernameAlreadyExists(UsernameAlreadyExists e) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
			.body(BodyErrorResponse.of(409, "CONFLICT", e.getMessage()));
	}

	@ExceptionHandler(UserNotFound.class)
	public ResponseEntity<BodyErrorResponse> handleUserNotFound(UserNotFound e) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(BodyErrorResponse.of(404, "NOT_FOUND", e.getMessage()));
	}

	@ExceptionHandler(TransactionNotFound.class)
	public ResponseEntity<BodyErrorResponse> handleTransactionNotFound(TransactionNotFound e) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(BodyErrorResponse.of(404, "NOT_FOUND", "Transaction not found"));
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<BodyErrorResponse> handleBadCredentials(BadCredentialsException e) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(BodyErrorResponse.of(404, "NOT_FOUND", "Invalid credentials"));
	}

	@ExceptionHandler(DisabledException.class)
	public ResponseEntity<BodyErrorResponse> handleDisabledUser(DisabledException e) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
			.body(BodyErrorResponse.of(409, "UNAUTHORIZED", e.getMessage()));
	}

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<BodyErrorResponse> handleAuthentication(AuthenticationException e) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
			.body(BodyErrorResponse.of(409, "UNAUTHORIZED", e.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
		public ResponseEntity<BodyErrorResponse> handleArgumentNotValid(MethodArgumentNotValidException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(BodyErrorResponse.of(400, "BAD_REQUEST", "Not valid arguments"));
	}

	@ExceptionHandler(MissingRequestHeaderException.class)
		public ResponseEntity<BodyErrorResponse> handleMissingRequestHeader(MissingRequestHeaderException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(BodyErrorResponse.of(400, "BAD_REQUEST", "Missing request header"));
	}

	@ExceptionHandler(Exception.class)
		public ResponseEntity<BodyErrorResponse> handleGenericErrors(Exception e) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(BodyErrorResponse.of(500, "INTERNAL_ERROR", e.getMessage()));
	}
}