package com.example.backend.dto;

import com.example.backend.validation.ValidCurrencies;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSettingsRequest {
    @NotNull(message = "Base currency is required")
    private Currency baseCurrency;

    @NotNull(message = "Watched currencies list is required")
    @ValidCurrencies
    private List<String> watchedCurrencies;

    @NotNull(message = "Language is required")
    private Language lang;
}
