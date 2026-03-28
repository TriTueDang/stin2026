package com.example.backend.service;


import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

@Service
@Slf4j
public class FileStorageService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public <T> void saveData(T data, String filepath) {
        try {
            File file = new File(filepath);
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }
            objectMapper.writeValue(file, data);
        } catch (Exception e) {
            log.error("Failed to save data to {}: {}", filepath, e.getMessage());
            throw new RuntimeException("Failed to save data to JSON", e);
        }
    }

    public <T> T loadData(String filepath, Class<T> clazz) {
        try {
            File file = new File(filepath);
            if (!file.exists()) {
                log.warn("File not found: {}", filepath);
                return null;
            }
            return objectMapper.readValue(file, clazz);
        } catch (Exception e) {
            log.error("Failed to load {} from {}: {}", clazz.getSimpleName(), filepath, e.getMessage());
            throw new RuntimeException("Failed to load data from JSON", e);
        }
    }
}