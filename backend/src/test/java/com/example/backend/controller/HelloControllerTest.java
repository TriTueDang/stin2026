package com.example.backend.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HelloControllerTest {

    private final HelloController helloController = new HelloController();
    @Test
    @DisplayName("Adding two numbers should return the correct sum")
    void testAddPositiveNumbers() {
        assertEquals(3, helloController.add(1, 2));
    }
}