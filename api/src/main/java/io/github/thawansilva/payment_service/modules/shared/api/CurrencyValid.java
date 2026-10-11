package io.github.thawansilva.payment_service.modules.shared.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.github.thawansilva.payment_service.modules.shared.internal.CurrencyValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = CurrencyValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrencyValid {
	String message() default "Invalid currency code.";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
