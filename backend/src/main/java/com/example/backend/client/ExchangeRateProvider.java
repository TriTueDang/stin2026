package com.example.backend.client;

import com.example.backend.dto.ExchangeRateResponse;
import com.example.backend.dto.TimeframeResponse;

public interface ExchangeRateProvider {
    ExchangeRateResponse getRates(String base);
    TimeframeResponse getTimeframeExchangeRates(String base, String startDate, String endDate);
}
