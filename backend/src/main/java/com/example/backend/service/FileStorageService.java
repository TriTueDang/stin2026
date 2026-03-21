package com.example.backend.service;

import com.example.backend.dto.ExchangeRateResponse;
import com.example.backend.dto.TimeframeResponse;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.io.File;

@Service
public class FileStorageService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void saveRates(ExchangeRateResponse response, String filepath) {
        try {
            File file = new File(filepath);
            file.getParentFile().mkdirs();
            objectMapper.writeValue(file, response);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save rates", e);
        }
    }
    public void saveRates(TimeframeResponse response, String filepath) {
        try {
            File file = new File(filepath);
            file.getParentFile().mkdirs();
            objectMapper.writeValue(file, response);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save rates", e);
        }
    }

    public ExchangeRateResponse loadRates(String filepath) {
        try {
            File file = new File(filepath);
            if (!file.exists()) {
                throw new RuntimeException("File not found: " + filepath);
            }
            return objectMapper.readValue(file, ExchangeRateResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load rates from JSON", e);
        }
    }

    public TimeframeResponse loadTimeframe(String filepath) {
        try {
            File file = new File(filepath);
            if (!file.exists()) {
                throw new RuntimeException("File not found: " + filepath);
            }
            return objectMapper.readValue(file, TimeframeResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load timeframe from JSON", e);
        }
    }

    public void saveSettings(com.example.backend.dto.UserSettings settings, String filepath) {
        try {
            File file = new File(filepath);
            file.getParentFile().mkdirs();
            objectMapper.writeValue(file, settings);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save settings", e);
        }
    }

    public com.example.backend.dto.UserSettings loadSettings(String filepath) {
        try {
            File file = new File(filepath);
            if (!file.exists()) {
                return new com.example.backend.dto.UserSettings();
            }
            return objectMapper.readValue(file, com.example.backend.dto.UserSettings.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load settings", e);
        }
    }
}