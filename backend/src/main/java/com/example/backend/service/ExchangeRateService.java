package com.example.backend.service;


import com.example.backend.client.ExchangeRateClient;
import com.example.backend.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExchangeRateService {

    private final ExchangeRateClient client;
    private final FileStorageService storage;
    private final StatisticsService statisticsService;

    @Value("${storage.path:data/}")
    private String storagePath;

    public ExchangeRateService(ExchangeRateClient client, FileStorageService storage, StatisticsService statisticsService) {
        this.client = client;
        this.storage = storage;
        this.statisticsService = statisticsService;
    }

    public CurrentRatesStatistics getCurrentRates(String base, List<String> watched) {

//        ExchangeRateResponse response = client.getRates(base);
//        storage.saveRates(response, storagePath + "rates4.json");
//        return response;
        ExchangeRateResponse response = storage.loadRates(storagePath + "rates-live.json");
        return statisticsService.calculateCurrentAll(response, watched);
    }
    public TimeframeResponse getHistoricalRates(String base, String startDate, String endDate) {
//        TimeframeResponse response = client.getTimeframeExchangeRates(base, startDate, endDate);
//        storage.saveRates(response, storagePath + "timeframe_rates.json");
//        return response;
        return storage.loadTimeframe(storagePath + "timeframe_rates.json");

    }
    public HistoricalRatesStatistics getHistoricalStatistics(String base, String startDate, String endDate, List<String> watched) {

        TimeframeResponse response = storage.loadTimeframe(storagePath + "timeframe_rates.json");
        return statisticsService.calculateTimeframeAll(response, watched);
    }

    public UserSettings getSettings() {
        return storage.loadSettings(storagePath + "user_settings.json");
    }

    public void saveSettings(UserSettings settings) {
        storage.saveSettings(settings, storagePath + "user_settings.json");
    }
}