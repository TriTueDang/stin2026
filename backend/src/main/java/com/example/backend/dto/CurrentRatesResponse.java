package com.example.backend.dto;

import lombok.Data;

@Data
public class CurrentRatesResponse {
    private ExchangeRateResponse exchangeRates;
    private String strongestCurrency;
    private String weakestCurrency;
}
