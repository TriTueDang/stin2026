package com.example.backend.dto;

import com.example.backend.validation.ValidDateRange;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ValidDateRange
public class HistoricalRatesRequest extends ExchangeRateRequestInterface {
    @NotBlank(message = "Start date is required")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Start date must be in YYYY-MM-DD format")
    private String startDate;

    @NotBlank(message = "End date is required")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "End date must be in YYYY-MM-DD format")
    private String endDate;
}
