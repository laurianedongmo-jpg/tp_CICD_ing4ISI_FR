package com.willbank.composite.controller;

import com.willbank.composite.service.ReleveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/composite")
@RequiredArgsConstructor
@Slf4j
public class ReleveController {
    
    private final ReleveService releveService;
    
    @GetMapping("/releve/{compteId}")
    public ResponseEntity<Map<String, Object>> getReleve(
            @PathVariable Long compteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFin) {
        log.info("GET /api/composite/releve/{} from {} to {}", compteId, dateDebut, dateFin);
        
        Map<String, Object> releve = releveService.getReleve(compteId, dateDebut, dateFin);
        return ResponseEntity.ok(releve);
    }
    
    @GetMapping("/comptes/{clientId}/overview")
    public ResponseEntity<Map<String, Object>> getCompteOverview(@PathVariable Long clientId) {
        log.info("GET /api/composite/comptes/{}/overview", clientId);
        
        Map<String, Object> overview = releveService.getCompteOverview(clientId);
        return ResponseEntity.ok(overview);
    }
}
