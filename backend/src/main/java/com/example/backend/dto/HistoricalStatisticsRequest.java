package com.example.backend.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class HistoricalStatisticsRequest extends ExchangeRateRequestInterface {
    @NotBlank(message = "Start date is required")
    private String startDate;
    @NotBlank(message = "End date is required")
    private String endDate;
    @NotEmpty(message = "Watched currencies list cannot be empty")
    private List<String> watched;
}
