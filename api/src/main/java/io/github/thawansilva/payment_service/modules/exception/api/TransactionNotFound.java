package io.github.thawansilva.payment_service.modules.exception.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TransactionNotFound extends RuntimeException {

	public TransactionNotFound(String transactionId) {
		super("Transaction not found: " + transactionId);
	}
}
