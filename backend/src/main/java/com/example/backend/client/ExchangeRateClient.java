package com.example.backend.client;

import com.example.backend.dto.ExchangeRateResponse;
import com.example.backend.dto.TimeframeResponse;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ExchangeRateClient {

    @Value("${exchange.api.url}")
    private String apiUrl;

    @Value("${exchange.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public ExchangeRateResponse getRates(String base) {

        // For live rates, use the /live endpoint
//        String url = apiUrl + "/live" + "?access_key=" + apiKey + "&source=" + base;
//        return restTemplate.getForObject(url, ExchangeRateResponse.class);

        // For historical rates, use the /historical endpoint with a specific date
        String url = apiUrl + "/historical" + "?access_key=" + apiKey + "&date=2026-03-15";
        return restTemplate.getForObject(url, ExchangeRateResponse.class);


    }
    public TimeframeResponse getTimeframeExchangeRates(String base, String startDate, String endDate) {
        String url = apiUrl + "/timeframe" + "?access_key=" + apiKey + "&source=" + base
                + "&start_date=" + startDate + "&end_date=" + endDate;
        return restTemplate.getForObject(url, TimeframeResponse.class);
    }
}
