package io.github.thawansilva.payment_service.modules.exception.internal;

public record BodyErrorResponse(int status, String error, String message) {

	public static BodyErrorResponse of(int status, String error, String message) {
		return new BodyErrorResponse(status, error, message);
	}
}
