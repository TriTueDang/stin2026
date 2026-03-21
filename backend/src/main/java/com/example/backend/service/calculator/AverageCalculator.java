package com.example.backend.service.calculator;

import com.example.backend.dto.HistoricalRatesStatistics;
import com.example.backend.dto.TimeframeResponse;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AverageCalculator implements HistoricalRateCalculator {

    @Override
    public void calculate(TimeframeResponse data, List<String> watchedCurrencies, HistoricalRatesStatistics results) {
        Map<String, Double> sums = new HashMap<>();
        Map<String, Integer> counts = new HashMap<>();
        String base = data.getSource();

        // Go through each day's quotes and accumulate sums and counts for each watched currency
        for (Map<String, Double> dailyQuotes : data.getQuotes().values()) {
            for (String currency : watchedCurrencies) {
                String key = base + currency;
                Double rate = dailyQuotes.get(key);

                if (rate != null) {
                    sums.put(currency, sums.getOrDefault(currency, 0.0) + rate);
                    counts.put(currency, counts.getOrDefault(currency, 0) + 1);
                }
            }
        }

        // Calculate averages
        Map<String, Double> averages = new HashMap<>();
        sums.forEach((currency, sum) -> {
            int count = counts.get(currency);
            averages.put(currency, sum / count);
        });

        results.setAverage(averages);
    }
}
