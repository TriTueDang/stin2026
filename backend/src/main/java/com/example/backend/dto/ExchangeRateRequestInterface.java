package com.example.backend.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import java.util.List;

@Data
public abstract class ExchangeRateRequestInterface {
    @NotBlank(message = "Base currency is required")
    @Pattern(regexp = "USD|EUR|CZK|GBP|CHF|JPY|PLN|HUF|AUD|CAD|CNY|SEK|NOK|DKK", message = "Unsupported base currency")
    private String base;
}
