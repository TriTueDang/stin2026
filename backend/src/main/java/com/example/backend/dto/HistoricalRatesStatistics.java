package com.example.backend.dto;

import lombok.Data;

import java.util.Map;

@Data
public class HistoricalRatesStatistics {
    private Map<String, Double> average;
}
