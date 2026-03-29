package com.example.backend.controller;

import com.example.backend.dto.*;
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
    public CurrentRatesResponse getCurrentRates(@Valid @RequestBody CurrentRateRequest request) {
        // return rates and max, min for watched currencies
        return service.getCurrentRates(request);
    }

    @PostMapping("/history")
    public HistoricalDataResponse getHistoricalData(@Valid @RequestBody HistoricalDataRequest request) {
        return service.getHistoricalData(request);
    }

    @GetMapping("/settings")
    public UserSettingsResponse getSettings() {
        return service.getSettings();
    }

    @PostMapping("/settings")
    public void saveSettings(@Valid @RequestBody UserSettingsRequest settings) {
        service.saveSettings(settings);
    }
}