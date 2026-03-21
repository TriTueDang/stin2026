package com.example.backend.service;


import com.example.backend.client.ExchangeRateClient;
import com.example.backend.dto.CurrentRatesStatistics;
import com.example.backend.dto.ExchangeRateResponse;
import com.example.backend.dto.HistoricalRatesStatistics;
import com.example.backend.dto.TimeframeResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExchangeRateService {

    private final ExchangeRateClient client;
    private final FileStorageService storage;
    private final StatisticsService statisticsService;

    public ExchangeRateService(ExchangeRateClient client, FileStorageService storage, StatisticsService statisticsService) {
        this.client = client;
        this.storage = storage;
        this.statisticsService = statisticsService;
    }

    public CurrentRatesStatistics getCurrentRates(String base, List<String> watched) {

//        ExchangeRateResponse response = client.getRates(base);
//        storage.saveRates(response, "data/rates4.json");
//        return response;
        ExchangeRateResponse response = storage.loadRates("data/rates-live.json");
        return statisticsService.calculateCurrentAll(response, watched);
    }
    public TimeframeResponse getHistoricalRates(String base, String startDate, String endDate) {
//        TimeframeResponse response = client.getTimeframeExchangeRates(base, startDate, endDate);
//        storage.saveRates(response, "data/timeframe_rates.json");
//        return response;
        return storage.loadTimeframe("data/timeframe_rates.json");

    }
    public HistoricalRatesStatistics getHistoricalStatistics(String base, String startDate, String endDate, List<String> watched) {

        TimeframeResponse response = storage.loadTimeframe("data/timeframe_rates.json");
        return statisticsService.calculateTimeframeAll(response, watched);
    }
}