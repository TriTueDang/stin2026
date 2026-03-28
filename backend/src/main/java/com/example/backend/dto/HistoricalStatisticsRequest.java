package com.example.backend.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class HistoricalStatisticsRequest extends ExchangeRateRequestInterface {
    @NotBlank(message = "Start date is required")
    private String startDate;
    @NotBlank(message = "End date is required")
    private String endDate;
}
