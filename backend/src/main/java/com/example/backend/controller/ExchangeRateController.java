package com.example.backend.controller;

import com.example.backend.dto.*;
import com.example.backend.service.ExchangeRateService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rates")
@Slf4j
public class ExchangeRateController {

    private final ExchangeRateService service;

    public ExchangeRateController(ExchangeRateService service) {
        this.service = service;
    }

    @PostMapping("/current")
    public CurrentRatesResponse getCurrentRates(@Valid @RequestBody CurrentRateRequest request) {
        log.info("Received request for current rates");
        return service.getCurrentRates(request);
    }

    @PostMapping("/history")
    public HistoricalDataResponse getHistoricalData(@Valid @RequestBody HistoricalDataRequest request) {
        log.info("Received request for historical data");
        return service.getHistoricalData(request);
    }

    @GetMapping("/settings")
    public UserSettingsResponse getSettings() {
        log.info("Received request to retrieve user settings");
        return service.getSettings();
    }

    @PostMapping("/settings")
    public void saveSettings(@Valid @RequestBody UserSettingsRequest settings) {
        log.info("Received request to save user settings");
        service.saveSettings(settings);
    }
}