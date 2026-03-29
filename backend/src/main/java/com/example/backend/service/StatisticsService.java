package com.example.backend.service;

import com.example.backend.dto.Currency;
import com.example.backend.dto.CurrentRatesResponse;
import com.example.backend.dto.ExchangeRateResponse;
import com.example.backend.dto.HistoricalRatesStatistics;
import com.example.backend.dto.TimeframeResponse;
import com.example.backend.service.calculator.CurrentRateCalculator;
import com.example.backend.service.calculator.HistoricalRateCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatisticsService {

    private final List<HistoricalRateCalculator> timeframeCalculators;
    private final List<CurrentRateCalculator> currentCalculators;

    @Autowired
    public StatisticsService(List<HistoricalRateCalculator> timeframeCalculators, List<CurrentRateCalculator> currentCalculators) {
        this.timeframeCalculators = timeframeCalculators;
        this.currentCalculators = currentCalculators;
    }

    /**
     * Vypočítá všechny statistiky pro časové období (zde průměr).
     */
    public HistoricalRatesStatistics calculateTimeframeAll(TimeframeResponse data, List<Currency> watchedCurrencies) {
        HistoricalRatesStatistics results = new HistoricalRatesStatistics();

        for (HistoricalRateCalculator calculator : timeframeCalculators) {
            calculator.calculate(data, watchedCurrencies, results);
        }

        return results;
    }

    /**
     * Calculates all current statistics based on the live exchange rate data and the list of watched currencies.
     */
    public CurrentRatesResponse calculateCurrentAll(ExchangeRateResponse data, List<Currency> watchedCurrencies) {
        CurrentRatesResponse results = new CurrentRatesResponse();
        results.setExchangeRates(data);

        for (CurrentRateCalculator calculator : currentCalculators) {
            calculator.calculate(data, watchedCurrencies, results);
          }

        return results;
    }
}
