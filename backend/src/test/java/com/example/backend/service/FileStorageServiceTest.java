package com.example.backend.service;

import com.example.backend.dto.BaseCurrency;
import com.example.backend.dto.UserSettingsResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FileStorageService Tests")
class FileStorageServiceTest {

    private final FileStorageService storage = new FileStorageService();

    @TempDir
    Path tempDir;

    @Nested
    @DisplayName("Black-Box Tests - Object Persistence")
    class BlackBoxTests {

        @Test
        void saveData_And_LoadData_Success() {
            String filepath = tempDir.resolve("test.json").toString();
            UserSettingsResponse data = new UserSettingsResponse();
            data.setBaseCurrency(BaseCurrency.USD);

            storage.saveData(data, filepath);
            UserSettingsResponse loaded = storage.loadData(filepath, UserSettingsResponse.class);

            assertNotNull(loaded);
            assertEquals(BaseCurrency.USD, loaded.getBaseCurrency());
        }

        @Test
        void saveData_CreatesMissingDirectories() {
            String filepath = tempDir.resolve("nested/dir/test.json").toString();
            UserSettingsResponse data = new UserSettingsResponse();

            storage.saveData(data, filepath);
            
            File file = new File(filepath);
            assertTrue(file.exists());
            assertTrue(file.getParentFile().exists());
        }
    }

    @Nested
    @DisplayName("White-Box Tests - Edge Cases & Failures")
    class WhiteBoxTests {

        @Test
        void saveData_HandlesNullParentFile() {
            // Case: simple filename without path (getParentFile() == null)
            String filepath = "test_simple.json";
            UserSettingsResponse data = new UserSettingsResponse();
            
            try {
                storage.saveData(data, filepath);
                File file = new File(filepath);
                assertTrue(file.exists());
                // Clean up
                file.delete();
            } finally {
                new File(filepath).delete();
            }
        }

        @Test
        void loadData_ReturnsNullIfFileNotFound() {
            String filepath = tempDir.resolve("nonexistent.json").toString();
            UserSettingsResponse loaded = storage.loadData(filepath, UserSettingsResponse.class);
            assertNull(loaded);
        }

        @Test
        void loadData_ThrowsOnInvalidJson() throws Exception {
            String filepath = tempDir.resolve("invalid.json").toString();
            java.nio.file.Files.writeString(tempDir.resolve("invalid.json"), "{\"invalid\": json}"); // Corrupt JSON

            assertThrows(RuntimeException.class, () -> 
                storage.loadData(filepath, UserSettingsResponse.class)
            );
        }

        @Test
        void saveData_ThrowsOnInvalidPath() {
            // Trying to save to a path that is actually an existing directory
            String filepath = tempDir.toString(); 

            assertThrows(RuntimeException.class, () -> 
                storage.saveData(new UserSettingsResponse(), filepath)
            );
        }
    }
}
