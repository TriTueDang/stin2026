package com.example.backend.client;

import com.example.backend.dto.ExchangeRateResponse;
import com.example.backend.dto.TimeframeResponse;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ExchangeRateClient {

    @Value("${exchange.api.url}")
    private String apiUrl;

    @Value("${exchange.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public ExchangeRateResponse getRates(String base) {

        // For live rates, use the /live endpoint
        String url = apiUrl + "/live" + "?access_key=" + apiKey + "&source=" + base;
        try {
            return restTemplate.getForObject(url, ExchangeRateResponse.class);
        } catch (RestClientException e) {
            log.error("API error while fetching live rates for base {}: {}", base, e.getMessage());
            throw e;
        }

        // For historical rates, use the /historical endpoint with a specific date
//        String url = apiUrl + "/historical" + "?access_key=" + apiKey + "&date=2026-03-15";
//        return restTemplate.getForObject(url, ExchangeRateResponse.class);


    }
    public TimeframeResponse getTimeframeExchangeRates(String base, String startDate, String endDate) {
        String url = apiUrl + "/timeframe" + "?access_key=" + apiKey + "&source=" + base
                + "&start_date=" + startDate + "&end_date=" + endDate;
        try {
            return restTemplate.getForObject(url, TimeframeResponse.class);
        } catch (RestClientException e) {
            log.error("API error while fetching timeframe rates for base {} from {} to {}: {}", base, startDate, endDate, e.getMessage());
            throw e;
        }
    }
}
