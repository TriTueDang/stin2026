package com.example.backend.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class HelloController {

    @Value("${exchange.api.url}")
    private String apiUrl;

    @Value("${exchange.api.key}")
    private String apiKey;

    // map to /api/hello so the frontend URL matches
    @GetMapping("/hello")
    public String hello() {
        return "Hello from Spring Boot!" + apiUrl;
    }
    public double divide(int a, int b) {
        if (b == 0) {
            throw new IllegalArgumentException("Divisor cannot be zero");
        }
        return (double) a / b;
    }


}