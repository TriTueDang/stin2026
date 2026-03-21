package com.example.backend.controller;

import com.example.backend.dto.CurrentRatesStatistics;
import com.example.backend.dto.HistoricalRatesStatistics;
import com.example.backend.dto.TimeframeResponse;
import com.example.backend.dto.UserSettings;
import com.example.backend.service.ExchangeRateService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rates")
public class ExchangeRateController {

    private final ExchangeRateService service;

    public ExchangeRateController(ExchangeRateService service) {
        this.service = service;
    }

    @GetMapping("/current/{base}")
    public CurrentRatesStatistics getCurrentRates(@PathVariable("base") String base, @RequestParam("watched") List<String> watched) {
        // return rates and max, min for watched currencies
        return service.getCurrentRates(base, watched);
    }

    @GetMapping("/history/{base}/{startDate}/{endDate}")
    public TimeframeResponse getHistoricalRates(@PathVariable("base") String base,@PathVariable("startDate") String startDate,@PathVariable("endDate") String endDate) {
        return service.getHistoricalRates(base, startDate, endDate);
    }


    @GetMapping("/history/statistics/{base}/{startDate}/{endDate}")
    public HistoricalRatesStatistics getHistoricalStatistics(@PathVariable("base") String base, @PathVariable("startDate") String startDate, @PathVariable("endDate") String endDate, @RequestParam("watched") List<String> watched) {
        return service.getHistoricalStatistics(base, startDate, endDate, watched);
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