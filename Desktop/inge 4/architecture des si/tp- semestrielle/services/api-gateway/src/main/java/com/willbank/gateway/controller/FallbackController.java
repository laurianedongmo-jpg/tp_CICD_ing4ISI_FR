package com.willbank.gateway.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
@Slf4j
public class FallbackController {
    
    @GetMapping("/client")
    public ResponseEntity<Map<String, Object>> clientFallback() {
        log.warn("Client Service fallback triggered");
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("error", "Service Unavailable");
        response.put("message", "Le service Client est temporairement indisponible. Veuillez réessayer plus tard.");
        response.put("service", "client-service");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
    
    @GetMapping("/compte")
    public ResponseEntity<Map<String, Object>> compteFallback() {
        log.warn("Compte Service fallback triggered");
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("error", "Service Unavailable");
        response.put("message", "Le service Compte est temporairement indisponible. Veuillez réessayer plus tard.");
        response.put("service", "compte-service");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
    
    @GetMapping("/transaction")
    public ResponseEntity<Map<String, Object>> transactionFallback() {
        log.warn("Transaction Service fallback triggered");
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("error", "Service Unavailable");
        response.put("message", "Le service Transaction est temporairement indisponible. Veuillez réessayer plus tard.");
        response.put("service", "transaction-service");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
    
    @GetMapping("/default")
    public ResponseEntity<Map<String, Object>> defaultFallback() {
        log.warn("Default fallback triggered");
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("error", "Service Unavailable");
        response.put("message", "Le service demandé est temporairement indisponible. Veuillez réessayer plus tard.");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
}
