package com.zanzibar.csms.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:9002", "http://localhost:3000"})
public class MinimalController {

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Backend is running!");
        response.put("status", "active");
        response.put("service", "CSMS Backend");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/test-connection")
    public ResponseEntity<Map<String, Object>> testConnection() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "HTTP connection established successfully");
        response.put("architecture", "Frontend (Next.js) -> HTTP/JSON -> Backend (Spring Boot)");
        return ResponseEntity.ok(response);
    }
}