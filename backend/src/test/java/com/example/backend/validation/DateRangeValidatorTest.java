package com.example.backend.validation;

import com.example.backend.dto.HistoricalDataRequest;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DateRangeValidator Tests")
class DateRangeValidatorTest {

    private final DateRangeValidator validator = new DateRangeValidator();

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder builder;

    @BeforeEach
    void setUp() {
        lenient().when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        lenient().when(builder.addConstraintViolation()).thenReturn(context);
    }

    @Nested
    @DisplayName("Black-Box Tests - Standard Validations")
    class BlackBoxTests {

        @Test
        void isValid_PositiveCase() {
            HistoricalDataRequest request = new HistoricalDataRequest();
            request.setStartDate("2023-01-01");
            request.setEndDate("2023-06-01");

            boolean result = validator.isValid(request, context);

            assertTrue(result);
            verify(context, never()).buildConstraintViolationWithTemplate(anyString());
        }
    }

    @Nested
    @DisplayName("White-Box Tests - Edge Cases & Violations")
    class WhiteBoxTests {

        @Test
        void isValid_HandlesInvalidType() {
            // Case: not an instance of HistoricalDataRequest
            boolean result = validator.isValid("Just a string", context);
            assertTrue(result);
        }

        @Test
        void isValid_HandlesNullDates() {
            HistoricalDataRequest request = new HistoricalDataRequest();
            request.setStartDate(null);
            request.setEndDate(null);

            boolean result = validator.isValid(request, context);
            assertTrue(result);
        }

        @Test
        void isValid_FailsBefore2020() {
            HistoricalDataRequest request = new HistoricalDataRequest();
            request.setStartDate("2019-12-31");
            request.setEndDate("2020-01-01");

            boolean result = validator.isValid(request, context);

            assertFalse(result);
            verify(context).buildConstraintViolationWithTemplate("Start date cannot be before year 2020");
        }

        @Test
        void isValid_FailsFutureDate() {
            HistoricalDataRequest request = new HistoricalDataRequest();
            request.setStartDate("2023-01-01");
            request.setEndDate(LocalDate.now().plusDays(1).toString());

            boolean result = validator.isValid(request, context);

            assertFalse(result);
            verify(context).buildConstraintViolationWithTemplate("End date cannot be in the future");
        }

        @Test
        void isValid_FailsStartAfterEnd() {
            HistoricalDataRequest request = new HistoricalDataRequest();
            request.setStartDate("2023-01-02");
            request.setEndDate("2023-01-01");

            boolean result = validator.isValid(request, context);

            assertFalse(result);
            verify(context).buildConstraintViolationWithTemplate("Start date cannot be after end date");
        }

        @Test
        void isValid_FailsRangeExceeded() {
            HistoricalDataRequest request = new HistoricalDataRequest();
            request.setStartDate("2022-01-01");
            request.setEndDate("2023-01-02"); // > 1 year

            boolean result = validator.isValid(request, context);

            assertFalse(result);
            verify(context).buildConstraintViolationWithTemplate("Date range cannot exceed 1 year");
        }

        @Test
        void isValid_HandlesParseException() {
            HistoricalDataRequest request = new HistoricalDataRequest();
            request.setStartDate("invalid-date");
            request.setEndDate("2023-01-01");

            boolean result = validator.isValid(request, context);
            assertTrue(result); // Logic: catch block returns true (delegated to @Pattern)
        }
    }
}
