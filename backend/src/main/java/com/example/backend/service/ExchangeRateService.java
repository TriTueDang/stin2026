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
        ExchangeRateResponse response = null;
        String warning = null;
        
        if (useRealApi) {
            try {
                response = client.getRates(request.getBase().name());
                if (response != null && response.isSuccess()) {
                    storage.saveData(response, storagePath + RATES_FILE);
                }
            } catch (Exception e) {
                log.error("API current rates failed: {}", e.getMessage());
                if (e.getMessage() != null && e.getMessage().contains("429")) {
                    warning = "Limit API překročen (429), zobrazuji poslední dostupná data z lokálního úložiště.";
                } else {
                    warning = "API selhalo, zobrazuji data z lokálního úložiště.";
                }
            }
        }

        if (response == null || !response.isSuccess()) {
            response = storage.loadData(storagePath + RATES_FILE, ExchangeRateResponse.class);
        }

        if (response == null) {
            throw new RuntimeException("Current exchange rates not available from API or storage");
        }

        log.info("Get current rates for base: {}, watched: {}, useRealApi: {}", request.getBase(), request.getWatched(), useRealApi);
        CurrentRatesResponse results = statisticsService.calculateCurrentAll(response, request.getWatched());
        results.setWarning(warning);
        return results;
    }

    public HistoricalDataResponse getHistoricalData(HistoricalDataRequest request) {
        String warning = null;
        TimeframeResponse fullResponse = null;
        
        try {
            fullResponse = getTimeframeData(request.getBase(), request.getStartDate(), request.getEndDate());
        } catch (Exception e) {
            log.error("Historical data retrieval failed: {}", e.getMessage());
            if (e.getMessage() != null && e.getMessage().contains("429")) {
                warning = "Limit API překročen (429), zobrazuji poslední dostupná data z lokálního úložiště.";
            } else {
                warning = "API selhalo, zobrazuji data z lokálního úložiště.";
            }
            // Fallback is handled inside getTimeframeData as well, but we catch it here to set the warning
            fullResponse = storage.loadData(storagePath + TIMEFRAME_FILE, TimeframeResponse.class);
        }

        if (fullResponse == null) {
            throw new RuntimeException("Timeframe data not available from API or storage");
        }

        HistoricalRatesStatistics stats = statisticsService.calculateTimeframeAll(fullResponse, request.getWatched());
        TimeframeResponse filteredResponse = filterTimeframeData(fullResponse, request.getWatched());

        return new HistoricalDataResponse(filteredResponse, stats, warning);
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

    private TimeframeResponse getTimeframeData(BaseCurrency base, String start, String end) {
        TimeframeResponse response = null;
        if (useRealApi) {
            try {
                response = client.getTimeframeExchangeRates(base.name(), start, end);
                if (response != null && response.isSuccess()) {
                    storage.saveData(response, storagePath + TIMEFRAME_FILE);
                    return response;
                }
            } catch (Exception e) {
                log.error("API timeframe failed: {}", e.getMessage());
                throw e; // Throw to be caught in getHistoricalData which sets the warning
            }
        }

        response = storage.loadData(storagePath + TIMEFRAME_FILE, TimeframeResponse.class);
        if (response == null) {
            throw new RuntimeException("Timeframe data not available from API or storage");
        }
        return response;
    }

    public UserSettingsResponse getSettings() {
        try {
            log.info("Loading user settings from storage");
            return storage.loadData(storagePath + SETTINGS_FILE, UserSettingsResponse.class);
        } catch (Exception e) {
            // If loading fails, return default settings
            UserSettingsResponse defaultSettings = new UserSettingsResponse();
            defaultSettings.setBaseCurrency(BaseCurrency.EUR);
            defaultSettings.setWatchedCurrencies(List.of(Currency.USD, Currency.EUR, Currency.GBP));
            defaultSettings.setLang(Language.cs);
            log.warn("Failed to get user settings, returning default settings: {}", e.getMessage());
            return defaultSettings;
        }
    }

    public void saveSettings(UserSettingsRequest settings) {
        try{
            storage.saveData(settings, storagePath + SETTINGS_FILE);
            log.info("Saving user settings: {}", settings.getBaseCurrency());
        }catch (Exception e) {
            log.error("Failed to save user settings: {}", e.getMessage());
            throw new RuntimeException("Failed to save user settings");
        }
    }
}