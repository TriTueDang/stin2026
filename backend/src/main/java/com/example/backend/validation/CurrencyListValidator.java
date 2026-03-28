package com.example.backend.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Set;

public class CurrencyListValidator implements ConstraintValidator<ValidCurrencies, List<String>> {

    private static final Set<String> SUPPORTED = Set.of(
            "USD", "EUR", "CZK", "GBP", "CHF", "JPY", "PLN", "HUF", "AUD", "CAD", "CNY", "SEK", "NOK", "DKK"
    );

    @Override
    public boolean isValid(List<String> value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // Use @NotEmpty for empty checks
        }

        for (String currency : value) {
            if (!SUPPORTED.contains(currency)) {
                return false;
            }
        }
        return true;
    }
}
