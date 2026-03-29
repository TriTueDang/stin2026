package com.example.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSettingsResponse {
    private BaseCurrency baseCurrency;
    private List<Currency> watchedCurrencies;
    private Language lang;
}
