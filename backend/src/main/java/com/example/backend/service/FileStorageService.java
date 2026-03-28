package com.example.backend.service;

import com.example.backend.dto.ExchangeRateResponse;
import com.example.backend.dto.TimeframeResponse;
import com.example.backend.dto.UserSettings;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

@Service
@Slf4j
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
            log.error("Failed to load rates from JSON {}: {}", filepath, e.getMessage());
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
            log.error("Failed to load timeframe from JSON {} : {}", filepath, e.getMessage());
            throw new RuntimeException("Failed to load timeframe from JSON", e);
        }
    }

    public void saveSettings(UserSettings settings, String filepath) {
        try {
            File file = new File(filepath);
            file.getParentFile().mkdirs();
            objectMapper.writeValue(file, settings);
        } catch (Exception e) {
            log.error("Failed to save settings to {} : {}", filepath, e.getMessage());
            throw new RuntimeException("Failed to save settings", e);
        }
    }

    public UserSettings loadSettings(String filepath) {
        try {
            File file = new File(filepath);
            if (!file.exists()) {
                throw new RuntimeException("File not found: " + filepath);
            }
            return objectMapper.readValue(file, UserSettings.class);
        } catch (Exception e) {
            log.error("Failed to load settings from {}: {}", filepath, e.getMessage());
            throw new RuntimeException("Failed to load settings", e);
        }
    }
}