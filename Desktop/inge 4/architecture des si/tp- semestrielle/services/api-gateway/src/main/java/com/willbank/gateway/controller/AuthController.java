package com.willbank.gateway.controller;

import com.willbank.gateway.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    
    private final JwtTokenProvider tokenProvider;
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        
        log.info("Login attempt for user: {}", username);
        
        // Authentification simplifiée pour les tests
        // Dans un vrai système, vérifier contre une base de données
        if ("admin".equals(username) && "admin123".equals(password)) {
            String token = tokenProvider.generateToken("1", Arrays.asList("ROLE_ADMIN", "ROLE_USER"));
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("type", "Bearer");
            response.put("userId", "1");
            response.put("username", username);
            response.put("roles", Arrays.asList("ROLE_ADMIN", "ROLE_USER"));
            
            log.info("Login successful for user: {}", username);
            return ResponseEntity.ok(response);
        } else if ("user".equals(username) && "user123".equals(password)) {
            String token = tokenProvider.generateToken("2", Arrays.asList("ROLE_USER"));
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("type", "Bearer");
            response.put("userId", "2");
            response.put("username", username);
            response.put("roles", Arrays.asList("ROLE_USER"));
            
            log.info("Login successful for user: {}", username);
            return ResponseEntity.ok(response);
        }
        
        log.warn("Login failed for user: {}", username);
        return ResponseEntity.status(401).body(Map.of(
            "error", "Unauthorized",
            "message", "Identifiants invalides"
        ));
    }
    
    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            
            if (tokenProvider.validateToken(token)) {
                String userId = tokenProvider.getUserIdFromToken(token);
                
                Map<String, Object> response = new HashMap<>();
                response.put("valid", true);
                response.put("userId", userId);
                response.put("roles", tokenProvider.getRolesFromToken(token));
                
                return ResponseEntity.ok(response);
            }
        }
        
        return ResponseEntity.status(401).body(Map.of(
            "valid", false,
            "message", "Token invalide"
        ));
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "api-gateway"));
    }
}
