package com.example.backend.service;

import com.example.backend.dto.Currency;
import com.example.backend.dto.CurrentRatesResponse;
import com.example.backend.dto.ExchangeRateResponse;
import com.example.backend.dto.HistoricalRatesStatistics;
import com.example.backend.dto.TimeframeResponse;
import com.example.backend.service.calculator.CurrentRateCalculator;
import com.example.backend.service.calculator.HistoricalRateCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StatisticsService Tests")
class StatisticsServiceTest {

    @Mock
    private CurrentRateCalculator currentCalculator;

    @Mock
    private HistoricalRateCalculator historicalCalculator;

    private StatisticsService statisticsService;

    @BeforeEach
    void setUp() {
        List<CurrentRateCalculator> currentCalculators = List.of(currentCalculator);
        List<HistoricalRateCalculator> historicalCalculators = List.of(historicalCalculator);
        statisticsService = new StatisticsService(historicalCalculators, currentCalculators);
    }

    @Nested
    @DisplayName("Black-Box Tests - Calculation Orchestration")
    class BlackBoxTests {

        @Test
        void calculateCurrentAll_CallsAllCurrentCalculators() {
            ExchangeRateResponse data = new ExchangeRateResponse();
            List<Currency> watched = List.of(Currency.USD);

            CurrentRatesResponse result = statisticsService.calculateCurrentAll(data, watched);

            assertNotNull(result);
            assertEquals(data, result.getExchangeRates());
            verify(currentCalculator).calculate(eq(data), eq(watched), any());
        }

        @Test
        void calculateTimeframeAll_CallsAllHistoricalCalculators() {
            TimeframeResponse data = new TimeframeResponse();
            List<Currency> watched = List.of(Currency.USD);

            HistoricalRatesStatistics result = statisticsService.calculateTimeframeAll(data, watched);

            assertNotNull(result);
            verify(historicalCalculator).calculate(eq(data), eq(watched), any());
        }
    }

    @Nested
    @DisplayName("White-Box Tests - Edge Cases")
    class WhiteBoxTests {

        @Test
        void calculateCurrentAll_HandlesEmptyCalculators() {
            StatisticsService emptyService = new StatisticsService(new ArrayList<>(), new ArrayList<>());
            
            CurrentRatesResponse result = emptyService.calculateCurrentAll(new ExchangeRateResponse(), Collections.emptyList());
            
            assertNotNull(result);
            assertNotNull(result.getExchangeRates());
        }

        @Test
        void calculateTimeframeAll_HandlesEmptyCalculators() {
            StatisticsService emptyService = new StatisticsService(new ArrayList<>(), new ArrayList<>());
            
            HistoricalRatesStatistics result = emptyService.calculateTimeframeAll(new TimeframeResponse(), Collections.emptyList());
            
            assertNotNull(result);
        }
    }
}
