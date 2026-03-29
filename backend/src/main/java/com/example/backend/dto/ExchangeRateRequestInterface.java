package com.example.backend.dto;

import lombok.Data;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
public abstract class ExchangeRateRequestInterface {
    @NotNull(message = "Base currency is required")
    private Currency base;

    @NotEmpty(message = "Watched currencies list cannot be empty")
    private List<Currency> watched;
}
