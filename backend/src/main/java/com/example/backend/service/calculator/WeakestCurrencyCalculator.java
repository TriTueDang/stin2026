package com.example.backend.service.calculator;

import com.example.backend.dto.CurrentRatesStatistics;
import com.example.backend.dto.ExchangeRateResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WeakestCurrencyCalculator implements CurrentRateCalculator {

    @Override
    public void calculate(ExchangeRateResponse data, List<String> watchedCurrencies, CurrentRatesStatistics results) {
        String base = data.getSource();
        String weakestCurrency = null;
        double lowestRate = Double.MAX_VALUE;

        for (String currency : watchedCurrencies) {
            String key = base + currency;
            
            if (data.getQuotes() != null) {
                Double rate = data.getQuotes().get(key);

                if (rate != null && rate < lowestRate) {
                    lowestRate = rate;
                    weakestCurrency = currency;
                }
            }
        }

        results.setWeakestCurrency(weakestCurrency);
    }
}
