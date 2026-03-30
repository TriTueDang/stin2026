package com.example.backend.service.calculator;

import com.example.backend.dto.Currency;
import com.example.backend.dto.CurrentRatesResponse;
import com.example.backend.dto.ExchangeRateResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("WeakestCurrencyCalculator Tests")
class WeakestCurrencyCalculatorTest {

    private final WeakestCurrencyCalculator calculator = new WeakestCurrencyCalculator();

    @Nested
    @DisplayName("Black-Box Tests - Trough Identification")
    class BlackBoxTests {

        @Test
        void calculate_CorrectWeakestCurrency() {
            ExchangeRateResponse data = new ExchangeRateResponse();
            data.setSource("EUR");
            data.setQuotes(Map.of("EURUSD", 1.0, "EURCZK", 25.0, "EURGBP", 0.8));

            List<Currency> watched = List.of(Currency.USD, Currency.CZK, Currency.GBP);
            CurrentRatesResponse results = new CurrentRatesResponse();

            calculator.calculate(data, watched, results);

            assertEquals("GBP", results.getWeakestCurrency());
        }
    }

    @Nested
    @DisplayName("White-Box Tests - Edge Cases")
    class WhiteBoxTests {

        @Test
        void calculate_HandlesNullQuotes() {
            ExchangeRateResponse data = new ExchangeRateResponse();
            data.setSource("EUR");
            data.setQuotes(null);

            CurrentRatesResponse results = new CurrentRatesResponse();
            calculator.calculate(data, List.of(Currency.USD), results);

            assertNull(results.getWeakestCurrency());
        }

        @Test
        void calculate_HandlesMissingWatchedCurrencies() {
            ExchangeRateResponse data = new ExchangeRateResponse();
            data.setSource("EUR");
            data.setQuotes(Map.of("EURGBP", 0.8));

            CurrentRatesResponse results = new CurrentRatesResponse();
            calculator.calculate(data, List.of(Currency.USD, Currency.CZK), results);

            assertNull(results.getWeakestCurrency());
        }

        @Test
        void calculate_HandlesEmptyWatchedList() {
            ExchangeRateResponse data = new ExchangeRateResponse();
            data.setSource("EUR");
            data.setQuotes(Map.of("EURUSD", 1.0));

            CurrentRatesResponse results = new CurrentRatesResponse();
            calculator.calculate(data, Collections.emptyList(), results);

            assertNull(results.getWeakestCurrency());
        }

        @Test
        void calculate_HandlesAllCurrenciesMissingInData() {
            ExchangeRateResponse data = new ExchangeRateResponse();
            data.setSource("EUR");
            data.setQuotes(Map.of("EURABC", 0.001));

            CurrentRatesResponse results = new CurrentRatesResponse();
            calculator.calculate(data, List.of(Currency.USD), results);

            assertNull(results.getWeakestCurrency());
        }
    }
}
