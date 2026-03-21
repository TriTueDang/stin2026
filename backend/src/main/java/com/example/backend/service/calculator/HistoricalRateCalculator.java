package com.example.backend.service.calculator;

import com.example.backend.dto.HistoricalRatesStatistics;
import com.example.backend.dto.TimeframeResponse;

import java.util.List;

public interface HistoricalRateCalculator {
    /**
     * Calculate a specific statistic based on the provided
     * exchange rate data and watched currencies, and store the result in the HistoricalRatesStatistics object.
     * @param data historical exchange rate data from the API
     * @param watchedCurrencies list of currencies
     * @param results object where the calculated statistic should be stored
     */
    void calculate(TimeframeResponse data, List<String> watchedCurrencies, HistoricalRatesStatistics results);
}
