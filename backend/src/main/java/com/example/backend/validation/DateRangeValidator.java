package com.example.backend.validation;

import com.example.backend.dto.HistoricalDataRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (!(value instanceof HistoricalDataRequest)) {
            return true;
        }

        HistoricalDataRequest request = (HistoricalDataRequest) value;
        String startDateStr = request.getStartDate();
        String endDateStr = request.getEndDate();

        if (startDateStr == null || endDateStr == null) {
            return true;
        }

        try {
            LocalDate start = LocalDate.parse(startDateStr);
            LocalDate end = LocalDate.parse(endDateStr);
            LocalDate now = LocalDate.now();
            LocalDate minDate = LocalDate.of(2020, 1, 1);

            // Constraint: Start date cannot be before 2020
            // api limit 1999-01-01
            if (start.isBefore(minDate)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Start date cannot be before year 2020")
                        .addConstraintViolation();
                return false;
            }

            // Constraint: End date cannot be in the future
            if (end.isAfter(now)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("End date cannot be in the future")
                        .addConstraintViolation();
                return false;
            }

            // Constraint: Start date cannot be after end date
            if (start.isAfter(end)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Start date cannot be after end date")
                        .addConstraintViolation();
                return false;
            }

            // Constraint: Max range 1 year
            if (start.plusYears(1).isBefore(end)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Date range cannot exceed 1 year")
                        .addConstraintViolation();
                return false;
            }

            return true;
        } catch (DateTimeParseException e) {
            return true; // Handled by @Pattern on individual fields
        }
    }
}
