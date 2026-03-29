package com.example.backend.service.calculator;

import com.example.backend.dto.Currency;
import com.example.backend.dto.CurrentRatesResponse;
import com.example.backend.dto.ExchangeRateResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WeakestCurrencyCalculator implements CurrentRateCalculator {

    @Override
    public void calculate(ExchangeRateResponse data, List<Currency> watchedCurrencies, CurrentRatesResponse results) {
        String base = data.getSource();
        String weakestCurrency = null;
        double lowestRate = Double.MAX_VALUE;

        for (Currency currency : watchedCurrencies) {
            String currencyName = currency.name();
            String key = base + currencyName;
            
            if (data.getQuotes() != null) {
                Double rate = data.getQuotes().get(key);

                if (rate != null && rate < lowestRate) {
                    lowestRate = rate;
                    weakestCurrency = currencyName;
                }
            }
        }

        results.setWeakestCurrency(weakestCurrency);
    }
}
