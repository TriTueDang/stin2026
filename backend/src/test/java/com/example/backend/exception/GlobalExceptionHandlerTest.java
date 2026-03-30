package com.example.backend.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Mock
    private BindingResult bindingResult;

    @Nested
    @DisplayName("White-Box Tests - Validation Exceptions")
    class ValidationTests {

        @Test
        void handleValidationExceptions_FlattenErrors() {
            FieldError fieldError = new FieldError("request", "base", "must not be null");
            ObjectError globalError = new ObjectError("request", "Date range invalid");
            
            when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
            when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
            when(bindingResult.getGlobalErrors()).thenReturn(List.of(globalError));

            ResponseEntity<Map<String, Object>> response = handler.handleValidationExceptions(methodArgumentNotValidException);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            List<String> errors = (List<String>) response.getBody().get("errors");
            assertTrue(errors.contains("base: must not be null"));
            assertTrue(errors.contains("request: Date range invalid"));
        }
    }

    @Nested
    @DisplayName("White-Box Tests - JSON Parsing Exceptions")
    class JsonParsingTests {

        @Test
        void handleJsonErrors_CurrencyError() {
            HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
            when(ex.getMessage()).thenReturn("Cannot deserialize value of type Currency");
            when(ex.getMostSpecificCause()).thenReturn(new RuntimeException("Specific reason"));

            ResponseEntity<Map<String, Object>> response = handler.handleJsonErrors(ex);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            List<String> errors = (List<String>) response.getBody().get("errors");
            assertTrue(errors.get(0).contains("Invalid currency"));
        }

        @Test
        void handleJsonErrors_LanguageError() {
            HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
            when(ex.getMessage()).thenReturn("Value Language.abc not supported");
            when(ex.getMostSpecificCause()).thenReturn(new RuntimeException("Specific reason"));

            ResponseEntity<Map<String, Object>> response = handler.handleJsonErrors(ex);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            List<String> errors = (List<String>) response.getBody().get("errors");
            assertTrue(errors.get(0).contains("Invalid language"));
        }

        @Test
        void handleJsonErrors_DefaultError() {
            HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
            when(ex.getMessage()).thenReturn("Generic JSON error");
            when(ex.getMostSpecificCause()).thenReturn(null);

            ResponseEntity<Map<String, Object>> response = handler.handleJsonErrors(ex);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            List<String> errors = (List<String>) response.getBody().get("errors");
            assertTrue(errors.get(0).contains("Invalid input format"));
            assertTrue(errors.get(1).contains("Unknown"));
        }
    }

    @Nested
    @DisplayName("Black-Box Tests - Generic Fallback")
    class GenericTests {

        @Test
        void handleAllExceptions_ReturnsInternalServerError() {
            Exception ex = new RuntimeException("Something went wrong");

            ResponseEntity<Map<String, Object>> response = handler.handleAllExceptions(ex);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertEquals("Something went wrong", response.getBody().get("message"));
            assertEquals("java.lang.RuntimeException", response.getBody().get("type"));
        }
    }
}
