package com.example.backend.service.calculator;

import com.example.backend.dto.CurrentRatesStatistics;
import com.example.backend.dto.ExchangeRateResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StrongestCurrencyCalculator implements CurrentRateCalculator {

    @Override
    public void calculate(ExchangeRateResponse data, List<String> watchedCurrencies, CurrentRatesStatistics results) {
        String base = data.getSource();
        String strongestCurrency = null;
        double highestRate = -1.0;

        for (String currency : watchedCurrencies) {
            String key = base + currency;
            
            if (data.getQuotes() != null) {
                Double rate = data.getQuotes().get(key);

                if (rate != null && rate > highestRate) {
                    highestRate = rate;
                    strongestCurrency = currency;
                }
            }
        }

        results.setStrongestCurrency(strongestCurrency);
    }
}
