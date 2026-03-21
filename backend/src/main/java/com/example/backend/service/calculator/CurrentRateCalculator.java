package com.example.backend.service.calculator;

import com.example.backend.dto.CurrentRatesStatistics;
import com.example.backend.dto.ExchangeRateResponse;

import java.util.List;

public interface CurrentRateCalculator {
    /**
     * Calculate a specific statistic based on current exchange rate data and watched currencies.
     */
    void calculate(ExchangeRateResponse data, List<String> watchedCurrencies, CurrentRatesStatistics results);
}
