package com.example.backend.validation;

import com.example.backend.dto.HistoricalRatesRequest;
import com.example.backend.dto.HistoricalStatisticsRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        String startDateStr;
        String endDateStr;

        if (value instanceof HistoricalRatesRequest) {
            startDateStr = ((HistoricalRatesRequest) value).getStartDate();
            endDateStr = ((HistoricalRatesRequest) value).getEndDate();
        } else if (value instanceof HistoricalStatisticsRequest) {
            startDateStr = ((HistoricalStatisticsRequest) value).getStartDate();
            endDateStr = ((HistoricalStatisticsRequest) value).getEndDate();
        } else {
            return true;
        }

        if (startDateStr == null || endDateStr == null) {
            return true;
        }

        try {
            LocalDate start = LocalDate.parse(startDateStr);
            LocalDate end = LocalDate.parse(endDateStr);

            if (start.isAfter(end)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Start date cannot be after end date")
                        .addConstraintViolation();
                return false;
            }

            long years = ChronoUnit.YEARS.between(start, end);
            if (years >= 1) {
                // Check if it's exactly 1 year or more
                if (years > 1 || end.isAfter(start.plusYears(1))) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate("Date range cannot exceed 1 year")
                            .addConstraintViolation();
                    return false;
                }
            }

            return true;
        } catch (DateTimeParseException e) {
            return true; // Handled by @Pattern on individual fields
        }
    }
}
