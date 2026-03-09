package com.example.backend.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class HelloController {

    // map to /api/hello so the frontend URL matches
    @GetMapping("/hello")
    public String hello() {
        add(2, 3);
        return "Hello from Spring Boot!";
    }

    public int add(int a, int b) {
        return a + b;
    }
}