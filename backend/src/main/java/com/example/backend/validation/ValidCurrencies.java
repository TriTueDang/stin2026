package com.example.backend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CurrencyListValidator.class)
@Documented
public @interface ValidCurrencies {
    String message() default "One or more currencies are unsupported";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
