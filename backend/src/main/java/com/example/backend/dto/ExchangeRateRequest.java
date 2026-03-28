package com.example.backend.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
@Data
@EqualsAndHashCode(callSuper = true)
public class ExchangeRateRequest extends ExchangeRateRequestInterface {
    @NotEmpty(message = "Watched currencies list cannot be empty")
    private List<String> watched;
}
