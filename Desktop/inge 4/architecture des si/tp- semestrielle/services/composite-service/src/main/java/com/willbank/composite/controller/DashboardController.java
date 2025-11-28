package com.willbank.composite.controller;

import com.willbank.composite.dto.DashboardDTO;
import com.willbank.composite.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/composite")
@RequiredArgsConstructor
@Slf4j
public class DashboardController {
    
    private final DashboardService dashboardService;
    
    @GetMapping("/dashboard/{clientId}")
    public CompletableFuture<ResponseEntity<DashboardDTO>> getDashboard(@PathVariable Long clientId) {
        log.info("GET /api/composite/dashboard/{}", clientId);
        
        return dashboardService.getDashboard(clientId)
                .thenApply(ResponseEntity::ok)
                .exceptionally(e -> {
                    log.error("Error getting dashboard: {}", e.getMessage());
                    return ResponseEntity.internalServerError().build();
                });
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Composite Service is UP");
    }
}
