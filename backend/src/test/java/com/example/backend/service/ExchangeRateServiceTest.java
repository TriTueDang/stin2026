package com.example.backend.service;

import com.example.backend.client.ExchangeRateProvider;
import com.example.backend.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ExchangeRateService Tests")
class ExchangeRateServiceTest {

    @Mock
    private ExchangeRateProvider client;

    @Mock
    private FileStorageService storage;

    @Mock
    private StatisticsService statisticsService;

    @InjectMocks
    private ExchangeRateService exchangeRateService;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(exchangeRateService, "storagePath", "test-data/");
        ReflectionTestUtils.setField(exchangeRateService, "useRealApi", true);
    }

    @Nested
    @DisplayName("Black-Box Tests - Functional Behavior")
    class BlackBoxTests {

        @Test
        void getCurrentRates_ReturnsSuccessfulData() {
            CurrentRateRequest request = createCurrentRequest();
            ExchangeRateResponse apiResponse = createResponse(true);
            CurrentRatesResponse stats = new CurrentRatesResponse();

            when(client.getRates(anyString())).thenReturn(apiResponse);
            when(statisticsService.calculateCurrentAll(any(), anyList())).thenReturn(stats);

            CurrentRatesResponse result = exchangeRateService.getCurrentRates(request);

            assertNotNull(result);
            assertNull(result.getWarning());
            verify(storage).saveData(eq(apiResponse), contains("rates-live.json"));
        }

        @Test
        void getHistoricalData_ReturnsFilteredData() {
            HistoricalDataRequest request = createHistoricalRequest();
            request.setWatched(List.of(Currency.USD));
            
            TimeframeResponse apiResponse = createTimeframeResponse(true);
            apiResponse.setSource("EUR");
            apiResponse.setQuotes(Map.of("2023-01-01", Map.of("EURUSD", 1.1)));

            when(client.getTimeframeExchangeRates(anyString(), any(), any())).thenReturn(apiResponse);
            when(statisticsService.calculateTimeframeAll(any(), any())).thenReturn(new HistoricalRatesStatistics());

            HistoricalDataResponse result = exchangeRateService.getHistoricalData(request);

            assertNotNull(result);
            assertEquals("EUR", result.getHistory().getSource());
            assertTrue(result.getHistory().getQuotes().get("2023-01-01").containsKey("EURUSD"));
        }

        @Test
        void getSettings_ReturnsSuccessfulData() {
            UserSettingsResponse storageResponse = new UserSettingsResponse();
            storageResponse.setBaseCurrency(BaseCurrency.USD);
            when(storage.loadData(anyString(), eq(UserSettingsResponse.class))).thenReturn(storageResponse);

            UserSettingsResponse result = exchangeRateService.getSettings();

            assertEquals(BaseCurrency.USD, result.getBaseCurrency());
        }

        @Test
        void getSettings_ReturnsDefaultIfLoadFails() {
            when(storage.loadData(anyString(), eq(UserSettingsResponse.class))).thenThrow(new RuntimeException());

            UserSettingsResponse result = exchangeRateService.getSettings();

            assertEquals(BaseCurrency.EUR, result.getBaseCurrency());
            assertEquals(Language.cs, result.getLang());
        }

        @Test
        void saveSettings_SavesCorrectData() {
            UserSettingsRequest req = new UserSettingsRequest();
            req.setBaseCurrency(BaseCurrency.USD);

            exchangeRateService.saveSettings(req);

            verify(storage).saveData(eq(req), contains("user_settings.json"));
        }
    }

    @Nested
    @DisplayName("White-Box Tests - Branch and Path Coverage")
    class WhiteBoxTests {

        @Test
        void fetchWithFallback_Handles429Error() {
            CurrentRateRequest request = createCurrentRequest();
            when(client.getRates(anyString())).thenThrow(new RuntimeException("Rate limit 429 exceeded"));
            
            ExchangeRateResponse storageResponse = createResponse(true);
            when(storage.loadData(anyString(), any())).thenReturn(storageResponse);
            when(statisticsService.calculateCurrentAll(any(), any())).thenReturn(new CurrentRatesResponse());

            CurrentRatesResponse result = exchangeRateService.getCurrentRates(request);

            assertTrue(result.getWarning().contains("429"));
        }

        @Test
        void fetchWithFallback_ThrowsWhenBothFail() {
            when(client.getRates(anyString())).thenThrow(new RuntimeException("API Fail"));
            when(storage.loadData(anyString(), any())).thenReturn(null);

            assertThrows(RuntimeException.class, () -> exchangeRateService.getCurrentRates(createCurrentRequest()));
        }

        @Test
        void isValid_TimeframeResponse_Failure() {
            // Case 1: TimeframeResponse success=false (White-box: !isValid branch)
            TimeframeResponse failResponse = createTimeframeResponse(false);
            when(client.getTimeframeExchangeRates(anyString(), any(), any())).thenReturn(failResponse);
            
            TimeframeResponse storageResponse = createTimeframeResponse(true);
            when(storage.loadData(anyString(), eq(TimeframeResponse.class))).thenReturn(storageResponse);
            when(statisticsService.calculateTimeframeAll(any(), any())).thenReturn(new HistoricalRatesStatistics());

            HistoricalDataResponse result = exchangeRateService.getHistoricalData(createHistoricalRequest());
            assertNotNull(result.getHistory());
            verify(storage).loadData(anyString(), eq(TimeframeResponse.class));
        }

        @Test
        void isValid_ExchangeRateResponse_Failure() {
            // Case 2: ExchangeRateResponse success=false
            ExchangeRateResponse failErr = createResponse(false);
            when(client.getRates(anyString())).thenReturn(failErr);
            
            when(storage.loadData(anyString(), eq(ExchangeRateResponse.class))).thenReturn(createResponse(true));
            when(statisticsService.calculateCurrentAll(any(), any())).thenReturn(new CurrentRatesResponse());

            exchangeRateService.getCurrentRates(createCurrentRequest());
            verify(storage).loadData(anyString(), eq(ExchangeRateResponse.class));
        }

        @Test
        void isValid_DefaultBranch() {
            // Testing the 'return response != null' branch for non-Response objects
            Boolean result = ReflectionTestUtils.invokeMethod(exchangeRateService, "isValid", "JustAString");
            assertEquals(true, result);

            Boolean resultNull = ReflectionTestUtils.invokeMethod(exchangeRateService, "isValid", (Object) null);
            assertEquals(false, resultNull);
        }

        @Test
        void filterTimeframeData_BranchCoverage() {
            // Case: watched currency mismatch prefix (White-box: filter branch)
            HistoricalDataRequest request = createHistoricalRequest();
            request.setWatched(List.of(Currency.USD));

            TimeframeResponse apiResponse = createTimeframeResponse(true);
            apiResponse.setSource("USD"); // Source is USD but quotes have EUR prefix
            apiResponse.setQuotes(Map.of("2023-01-01", Map.of("EURUSD", 1.1)));

            when(client.getTimeframeExchangeRates(anyString(), any(), any())).thenReturn(apiResponse);
            when(statisticsService.calculateTimeframeAll(any(), any())).thenReturn(new HistoricalRatesStatistics());

            HistoricalDataResponse result = exchangeRateService.getHistoricalData(request);
            // Result should be empty because prefix (USD) + USD doesn't match EURUSD
            assertTrue(result.getHistory().getQuotes().get("2023-01-01").isEmpty());
        }

        @Test
        void executeAndIgnore_HandlesSettingsSaveError() {
            doThrow(new RuntimeException("Storage Error")).when(storage).saveData(any(), anyString());
            
            // Should not throw
            assertDoesNotThrow(() -> exchangeRateService.saveSettings(new UserSettingsRequest()));
            verify(storage).saveData(any(), anyString());
        }

        @Test
        void safeLoadSettings_ReturnsNullOnException() {
            when(storage.loadData(anyString(), eq(UserSettingsResponse.class))).thenThrow(new RuntimeException());
            
            // Testing branch in safeLoadSettings via getSettings
            UserSettingsResponse result = exchangeRateService.getSettings();
            assertNotNull(result); // result from getDefaultSettings
        }

        @Test
        void useRealApi_FalseBranch() {
            ReflectionTestUtils.setField(exchangeRateService, "useRealApi", false);
            
            when(storage.loadData(anyString(), eq(ExchangeRateResponse.class))).thenReturn(createResponse(true));
            when(statisticsService.calculateCurrentAll(any(), any())).thenReturn(new CurrentRatesResponse());

            exchangeRateService.getCurrentRates(createCurrentRequest());

            verify(client, never()).getRates(anyString());
        }
    }

    // --- Helpers ---

    private CurrentRateRequest createCurrentRequest() {
        CurrentRateRequest req = new CurrentRateRequest();
        req.setBase(BaseCurrency.EUR);
        req.setWatched(List.of(Currency.USD));
        return req;
    }

    private HistoricalDataRequest createHistoricalRequest() {
        HistoricalDataRequest req = new HistoricalDataRequest();
        req.setBase(BaseCurrency.EUR);
        req.setStartDate("2023-01-01");
        req.setEndDate("2023-01-02");
        return req;
    }

    private ExchangeRateResponse createResponse(boolean success) {
        ExchangeRateResponse res = new ExchangeRateResponse();
        res.setSuccess(success);
        return res;
    }

    private TimeframeResponse createTimeframeResponse(boolean success) {
        TimeframeResponse res = new TimeframeResponse();
        res.setSuccess(success);
        return res;
    }
}

