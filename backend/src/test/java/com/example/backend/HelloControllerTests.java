package com.example.backend;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.example.backend.controller.HelloController;
import static org.junit.jupiter.api.Assertions.*;

public class HelloControllerTests {

    private HelloController helloController;

    @BeforeEach
    public void setUp() {
        helloController = new HelloController();
    }

    @Test
    public void testDivide() {
        // Test normal division
        assertEquals(2.0, helloController.divide(4, 2), 0.001);
        // Test division by zero
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            helloController.divide(4, 0);
        });
        assertEquals("Divisor cannot be zero", exception.getMessage());
    }
}
