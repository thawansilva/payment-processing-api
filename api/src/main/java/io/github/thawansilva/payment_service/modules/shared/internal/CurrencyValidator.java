package io.github.thawansilva.payment_service.modules.shared.internal;

import java.util.Currency;

import io.github.thawansilva.payment_service.modules.shared.api.CurrencyValid;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CurrencyValidator implements ConstraintValidator<CurrencyValid, String> {

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null) {
			return true;
		}
		try {
			Currency.getInstance(value);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}
}
