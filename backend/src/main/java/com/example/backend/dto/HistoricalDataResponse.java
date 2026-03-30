package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoricalDataResponse {
    private TimeframeResponse history;
    private HistoricalRatesStatistics statistics;
    private String warning;
}
