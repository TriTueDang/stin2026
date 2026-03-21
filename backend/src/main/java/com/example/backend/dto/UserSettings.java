package com.example.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class UserSettings {
    private String baseCurrency;
    private List<String> watchedCurrencies;
    private String lang;
}
