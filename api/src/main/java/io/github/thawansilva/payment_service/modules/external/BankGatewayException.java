package io.github.thawansilva.payment_service.modules.external;

public class BankGatewayException extends RuntimeException {
	public BankGatewayException(String message) {
		super(message);
	}
}
