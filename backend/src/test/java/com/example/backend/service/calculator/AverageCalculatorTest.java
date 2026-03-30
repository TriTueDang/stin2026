package com.example.backend.service.calculator;

import com.example.backend.dto.Currency;
import com.example.backend.dto.HistoricalRatesStatistics;
import com.example.backend.dto.TimeframeResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AverageCalculator Tests")
class AverageCalculatorTest {

    private final AverageCalculator calculator = new AverageCalculator();

    @Nested
    @DisplayName("Black-Box Tests - Standard Calculations")
    class BlackBoxTests {

        @Test
        void calculate_CorrectAverages() {
            TimeframeResponse data = new TimeframeResponse();
            data.setSource("EUR");
            Map<String, Map<String, Double>> quotes = new HashMap<>();
            quotes.put("2023-01-01", Map.of("EURUSD", 1.0, "EURCZK", 20.0));
            quotes.put("2023-01-02", Map.of("EURUSD", 2.0, "EURCZK", 30.0));
            data.setQuotes(quotes);

            List<Currency> watched = List.of(Currency.USD, Currency.CZK);
            HistoricalRatesStatistics results = new HistoricalRatesStatistics();

            calculator.calculate(data, watched, results);

            Map<String, Double> averages = results.getAverage();
            assertEquals(1.5, averages.get("USD"), 0.001); // (1+2)/2
            assertEquals(25.0, averages.get("CZK"), 0.001); // (20+30)/2
        }
    }

    @Nested
    @DisplayName("White-Box Tests - Edge Cases")
    class WhiteBoxTests {

        @Test
        void calculate_HandlesMissingRatesOnCertainDays() {
            TimeframeResponse data = new TimeframeResponse();
            data.setSource("EUR");
            Map<String, Map<String, Double>> quotes = new HashMap<>();
            quotes.put("2023-01-01", Map.of("EURUSD", 1.0));
            quotes.put("2023-01-02", Map.of("EURCZK", 30.0)); // Missing USD
            data.setQuotes(quotes);

            List<Currency> watched = List.of(Currency.USD, Currency.CZK);
            HistoricalRatesStatistics results = new HistoricalRatesStatistics();

            calculator.calculate(data, watched, results);

            Map<String, Double> averages = results.getAverage();
            assertEquals(1.0, averages.get("USD"), 0.001); // 1.0 / 1
            assertEquals(30.0, averages.get("CZK"), 0.001); // 30.0 / 1
        }

        @Test
        void calculate_HandlesEmptyWatchedList() {
            TimeframeResponse data = new TimeframeResponse();
            data.setSource("EUR");
            data.setQuotes(Map.of("2023-01-01", Map.of("EURUSD", 1.0)));

            HistoricalRatesStatistics results = new HistoricalRatesStatistics();
            calculator.calculate(data, Collections.emptyList(), results);

            assertTrue(results.getAverage().isEmpty());
        }

        @Test
        void calculate_HandlesMismatchingCurrencyPrefix() {
            TimeframeResponse data = new TimeframeResponse();
            data.setSource("USD"); // Base is USD
            data.setQuotes(Map.of("2023-01-01", Map.of("EURUSD", 1.0))); // Key is EURUSD instead of USDUSD

            HistoricalRatesStatistics results = new HistoricalRatesStatistics();
            calculator.calculate(data, List.of(Currency.USD), results);

            assertTrue(results.getAverage().isEmpty());
        }
    }
}
