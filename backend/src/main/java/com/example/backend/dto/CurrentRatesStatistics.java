package com.example.backend.dto;

import lombok.Data;

@Data
public class CurrentRatesStatistics {
    private ExchangeRateResponse exchangeRates;
    private String strongestCurrency;
    private String weakestCurrency;
}
