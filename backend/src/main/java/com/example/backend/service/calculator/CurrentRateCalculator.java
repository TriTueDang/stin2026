package com.example.backend.service.calculator;

import com.example.backend.dto.Currency;
import com.example.backend.dto.CurrentRatesResponse;
import com.example.backend.dto.ExchangeRateResponse;

import java.util.List;

public interface CurrentRateCalculator {
    /**
     * Calculate a specific statistic based on current exchange rate data and watched currencies.
     */
    void calculate(ExchangeRateResponse data, List<Currency> watchedCurrencies, CurrentRatesResponse results);
}
