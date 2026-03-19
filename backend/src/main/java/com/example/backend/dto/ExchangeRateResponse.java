package com.example.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Map;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangeRateResponse {

    private boolean success;

    private String source;

    private Long timestamp;

    private Map<String, Double> quotes  ;
}
