package com.example.backend.service;


import com.example.backend.client.ExchangeRateClient;
import com.example.backend.dto.ExchangeRateResponse;
import com.example.backend.dto.TimeframeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
public class ExchangeRateService {

    private final ExchangeRateClient client;
    private final FileStorageService storage;

    public ExchangeRateService(ExchangeRateClient client, FileStorageService storage) {
        this.client = client;
        this.storage = storage;
    }

    public ExchangeRateResponse getRates(String base) {

//        ExchangeRateResponse response = client.getRates(base);
//        storage.saveRates(response, "data/rates4.json");
//        return response;
        return storage.loadRates("data/rates-live.json");
    }
    public TimeframeResponse getTimeframeExchangeRates(String base, String startDate, String endDate) {
//        TimeframeResponse response = client.getTimeframeExchangeRates(base, startDate, endDate);
//        storage.saveRates(response, "data/timeframe_rates.json");
//        return response;
        return storage.loadTimeframe("data/timeframe_rates.json");
    }
}