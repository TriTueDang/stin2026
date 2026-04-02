package com.example.backend.service;

import com.example.backend.dto.BaseCurrency;
import com.example.backend.dto.Currency;
import com.example.backend.dto.CurrentRatesResponse;
import com.example.backend.dto.CurrentRateRequest;
import com.example.backend.dto.ExchangeRateResponse;
import com.example.backend.dto.HistoricalDataResponse;
import com.example.backend.dto.HistoricalDataRequest;
import com.example.backend.dto.HistoricalRatesStatistics;
import com.example.backend.dto.TimeframeResponse;
import com.example.backend.dto.UserSettingsResponse;
import com.example.backend.dto.UserSettingsRequest;
import com.example.backend.client.ExchangeRateProvider;
import com.example.backend.dto.Language;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ExchangeRateService {

    private static final String RATES_FILE = "rates-live.json";
    private static final String TIMEFRAME_FILE = "timeframe_rates.json";
    private static final String SETTINGS_FILE = "user_settings.json";

    private final ExchangeRateProvider client;
    private final FileStorageService storage;
    private final StatisticsService statisticsService;

    @Value("${storage.path:data/}")
    private String storagePath;

    @Value("${app.use-real-api:false}")
    private boolean useRealApi;

    public ExchangeRateService(ExchangeRateProvider client, FileStorageService storage,
            StatisticsService statisticsService) {
        this.client = client;
        this.storage = storage;
        this.statisticsService = statisticsService;
    }

    public CurrentRatesResponse getCurrentRates(CurrentRateRequest request) {

        FetchResult<ExchangeRateResponse> result = fetchWithFallback(
                () -> client.getRates(request.getBase().name()),
                response -> storage.saveData(response, getPath(RATES_FILE)),
                () -> storage.loadData(getPath(RATES_FILE), ExchangeRateResponse.class),
                "current rates");

        CurrentRatesResponse response = statisticsService.calculateCurrentAll(result.data(), request.getWatched());
        response.setWarning(result.warning());
        return response;
    }

    public HistoricalDataResponse getHistoricalData(HistoricalDataRequest request) {

        FetchResult<TimeframeResponse> result = fetchWithFallback(
                () -> client.getTimeframeExchangeRates(request.getBase().name(), request.getStartDate(),
                        request.getEndDate()),
                response -> storage.saveData(response, getPath(TIMEFRAME_FILE)),
                () -> storage.loadData(getPath(TIMEFRAME_FILE), TimeframeResponse.class),
                "historical rates");

        HistoricalRatesStatistics stats = statisticsService.calculateTimeframeAll(result.data(), request.getWatched());
        TimeframeResponse filtered = filterTimeframeData(result.data(), request.getWatched());

        return new HistoricalDataResponse(filtered, stats, result.warning());
    }

    public UserSettingsResponse getSettings() {
        return Optional.ofNullable(safeLoadSettings())
                .orElseGet(this::getDefaultSettings);
    }

    public void saveSettings(UserSettingsRequest settings) {
        executeAndIgnore(() -> storage.saveData(settings, getPath(SETTINGS_FILE)), "save settings");
    }

    // --- Helper Methods & Internal Classes ---

    private record FetchResult<T>(T data, String warning) {
    }

    @FunctionalInterface
    private interface FailableSupplier<T> {
        T get() throws Exception;
    }

    @FunctionalInterface
    private interface FailableAction {
        void execute() throws Exception;
    }

    private <T> FetchResult<T> fetchWithFallback(FailableSupplier<T> apiCall, Consumer<T> onApiSuccess,
            Supplier<T> fallbackCall, String context) {
        T response = null;
        String warning = null;

        if (useRealApi) {
            try {
                response = apiCall.get();
                if (isValid(response)) {
                    onApiSuccess.accept(response);
                }
            } catch (Exception e) {
                log.error("API {} failed: {}", context, e.getMessage());
                warning = determineWarning(e);
            }
        }

        if (response == null || !isValid(response)) {
            response = fallbackCall.get();
        }

        if (response == null) {
            throw new RuntimeException("Data for " + context + " not available from API or storage");
        }

        return new FetchResult<>(response, warning);
    }

    private boolean isValid(Object response) {
        if (response instanceof ExchangeRateResponse err)
            return err.isSuccess();
        if (response instanceof TimeframeResponse tr)
            return tr.isSuccess();
        return response != null;
    }

    private String determineWarning(Exception e) {
        String msg = e.getMessage() != null ? e.getMessage() : "";
        if (msg.contains("429")) {
            return "Limit API překročen (429), zobrazuji poslední dostupná data z lokálního úložiště.";
        }
        return "API selhalo, zobrazuji data z lokálního úložiště.";
    }

    private String getPath(String fileName) {
        return storagePath + fileName;
    }

    private void executeAndIgnore(FailableAction action, String context) {
        try {
            action.execute();
        } catch (Exception e) {
            log.error("Failed to {}: {}", context, e.getMessage());
        }
    }

    private UserSettingsResponse safeLoadSettings() {
        try {
            return storage.loadData(getPath(SETTINGS_FILE), UserSettingsResponse.class);
        } catch (Exception e) {
            log.warn("Failed to load user settings: {}", e.getMessage());
            return null;
        }
    }

    private UserSettingsResponse getDefaultSettings() {
        UserSettingsResponse defaultSettings = new UserSettingsResponse();
        defaultSettings.setBaseCurrency(BaseCurrency.EUR);
        defaultSettings.setWatchedCurrencies(List.of(Currency.USD, Currency.EUR, Currency.GBP));
        defaultSettings.setLang(Language.cs);
        return defaultSettings;
    }

    private TimeframeResponse filterTimeframeData(TimeframeResponse original, List<Currency> watched) {
        if (original == null || original.getQuotes() == null)
            return original;

        TimeframeResponse filtered = new TimeframeResponse();
        filtered.setSuccess(original.isSuccess());
        filtered.setSource(original.getSource());
        filtered.setStart_date(original.getStart_date());
        filtered.setEnd_date(original.getEnd_date());

        String prefix = original.getSource();
        Map<String, Map<String, Double>> filteredQuotes = original.getQuotes().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> watched.stream()
                                .map(currency -> prefix + currency.name())
                                .filter(entry.getValue()::containsKey)
                                .collect(Collectors.toMap(k -> k, k -> entry.getValue().get(k)))));

        filtered.setQuotes(filteredQuotes);
        return filtered;
    }
}
