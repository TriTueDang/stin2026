package com.example.backend.controller;

import com.example.backend.dto.ExchangeRateResponse;
import com.example.backend.dto.TimeframeResponse;
import com.example.backend.service.ExchangeRateService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rates")
public class ExchangeRateController {

    private final ExchangeRateService service;

    public ExchangeRateController(ExchangeRateService service) {
        this.service = service;
    }

    @GetMapping("/{base}")
    public ExchangeRateResponse getRates(@PathVariable String base) {
        return service.getRates(base);
    }
    @GetMapping("/timeframe/{base}/{startDate}/{endDate}")
    public TimeframeResponse getTimeframeExchangeRates(@PathVariable String base,@PathVariable String startDate,@PathVariable String endDate) {
        return service.getTimeframeExchangeRates(base, startDate, endDate);
    }
}