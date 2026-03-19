package com.example.backend.dto;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TimeframeResponse {
    private boolean success;

    private String source;

    private String start_date;

    private String end_date;

    private Map<String, Map<String, Double>> quotes;
}
