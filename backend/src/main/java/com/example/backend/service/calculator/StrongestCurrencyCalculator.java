package com.example.backend.service.calculator;

import com.example.backend.dto.Currency;
import com.example.backend.dto.CurrentRatesResponse;
import com.example.backend.dto.ExchangeRateResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StrongestCurrencyCalculator implements CurrentRateCalculator {

    @Override
    public void calculate(ExchangeRateResponse data, List<Currency> watchedCurrencies, CurrentRatesResponse results) {
        String base = data.getSource();
        String strongestCurrency = null;
        double highestRate = -1.0;

        for (Currency currency : watchedCurrencies) {
            String currencyName = currency.name();
            String key = base + currencyName;
            
            if (data.getQuotes() != null) {
                Double rate = data.getQuotes().get(key);

                if (rate != null && rate > highestRate) {
                    highestRate = rate;
                    strongestCurrency = currencyName;
                }
            }
        }

        results.setStrongestCurrency(strongestCurrency);
    }
}
