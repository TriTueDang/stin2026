package com.example.backend.controller;

import com.example.backend.dto.CurrentRatesStatistics;
import com.example.backend.dto.HistoricalRatesStatistics;
import com.example.backend.dto.TimeframeResponse;
import com.example.backend.dto.UserSettings;
import com.example.backend.dto.ExchangeRateRequest;
import com.example.backend.dto.HistoricalStatisticsRequest;
import com.example.backend.dto.HistoricalRatesRequest;
import com.example.backend.service.ExchangeRateService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rates")
public class ExchangeRateController {

    private final ExchangeRateService service;

    public ExchangeRateController(ExchangeRateService service) {
        this.service = service;
    }

    @PostMapping("/current")
    public CurrentRatesStatistics getCurrentRates(@Valid @RequestBody ExchangeRateRequest request) {
        // return rates and max, min for watched currencies
        return service.getCurrentRates(request.getBase(), request.getWatched());
    }

    @PostMapping("/history")
    public TimeframeResponse getHistoricalRates(@Valid @RequestBody HistoricalRatesRequest request) {
        return service.getHistoricalRates(request.getBase(), request.getStartDate(), request.getEndDate());
    }


    @PostMapping("/history/statistics")
    public HistoricalRatesStatistics getHistoricalStatistics(@Valid @RequestBody HistoricalStatisticsRequest request) {
        return service.getHistoricalStatistics(request.getBase(), request.getStartDate(), request.getEndDate(), request.getWatched());
    }

    @GetMapping("/settings")
    public UserSettings getSettings() {
        return service.getSettings();
    }

    @PostMapping("/settings")
    public void saveSettings(@RequestBody UserSettings settings) {
        service.saveSettings(settings);
    }
}