package com.example.backend.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

@Data
public abstract class ExchangeRateRequestInterface {
    @NotBlank(message = "Base currency is required")
    @Size(min = 3, max = 3, message = "Base currency must be 3 characters")
    private String base;

    @NotEmpty(message = "Watched currencies list cannot be empty")
    private List<String> watched;
}
