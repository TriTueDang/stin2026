package com.example.backend.dto;

import com.example.backend.validation.ValidCurrencies;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import java.util.List;
@Data
@EqualsAndHashCode(callSuper = true)
public class ExchangeRateRequest extends ExchangeRateRequestInterface {
    @NotEmpty(message = "Watched currencies list cannot be empty")
    @ValidCurrencies
    private List<String> watched;
}
