package com.willbank.compte.controller;

import com.willbank.compte.dto.CompteDTO;
import com.willbank.compte.dto.CompteRequest;
import com.willbank.compte.dto.SoldeUpdateRequest;
import com.willbank.compte.entity.Compte;
import com.willbank.compte.service.CompteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comptes")
@RequiredArgsConstructor
@Slf4j
public class CompteController {
    
    private final CompteService compteService;
    
    @PostMapping("/test-json")
    public ResponseEntity<Map<String, Object>> debugPost(@RequestBody CompteRequest request) {
        log.info("POST /api/comptes/test-json - Request received");
        log.info("clientId: {} (type: {})", request.getClientId(), 
                request.getClientId() != null ? request.getClientId().getClass().getSimpleName() : "null");
        log.info("typeCompte: '{}' (type: {})", request.getTypeCompte(),
                request.getTypeCompte() != null ? request.getTypeCompte().getClass().getSimpleName() : "null");
        
        Map<String, Object> response = Map.of(
            "clientId", request.getClientId() != null ? request.getClientId() : "NULL",
            "typeCompte", request.getTypeCompte() != null ? request.getTypeCompte() : "NULL",
            "devise", request.getDevise() != null ? request.getDevise() : "NULL",
            "decouvertAutorise", request.getDecouvertAutorise() != null ? request.getDecouvertAutorise() : "NULL",
            "tauxInteret", request.getTauxInteret() != null ? request.getTauxInteret() : "NULL"
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CompteDTO> createCompte(@RequestBody CompteRequest request) {
        try {
            log.info("POST /api/comptes - Raw request received");
            log.info("Request clientId: {}", request.getClientId());
            log.info("Request typeCompte: '{}'", request.getTypeCompte());
            log.info("Request devise: '{}'", request.getDevise());
            log.info("Request decouvertAutorise: {}", request.getDecouvertAutorise());
            log.info("Request tauxInteret: {}", request.getTauxInteret());
            log.info("Full request object: {}", request);
            
            CompteDTO compte = compteService.createCompte(request);
            log.info("Compte created successfully: {}", compte);
            return ResponseEntity.status(HttpStatus.CREATED).body(compte);
        } catch (Exception e) {
            log.error("Error creating compte: ", e);
            throw e;
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CompteDTO> getCompteById(@PathVariable Long id) {
        log.info("GET /api/comptes/{}", id);
        CompteDTO compte = compteService.getCompteById(id);
        return ResponseEntity.ok(compte);
    }
    
    @GetMapping("/numero/{numeroCompte}")
    public ResponseEntity<CompteDTO> getCompteByNumero(@PathVariable String numeroCompte) {
        log.info("GET /api/comptes/numero/{}", numeroCompte);
        CompteDTO compte = compteService.getCompteByNumero(numeroCompte);
        return ResponseEntity.ok(compte);
    }
    
    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<CompteDTO>> getComptesByClient(@PathVariable Long clientId) {
        log.info("GET /api/comptes/client/{}", clientId);
        List<CompteDTO> comptes = compteService.getComptesByClient(clientId);
        return ResponseEntity.ok(comptes);
    }
    
    @PatchMapping("/{id}/solde")
    public ResponseEntity<CompteDTO> updateSolde(
            @PathVariable Long id,
            @Valid @RequestBody SoldeUpdateRequest request) {
        log.info("PATCH /api/comptes/{}/solde - operation: {}, montant: {}", 
                id, request.getOperation(), request.getMontant());
        CompteDTO compte = compteService.updateSolde(id, request);
        return ResponseEntity.ok(compte);
    }
    
    @PatchMapping("/{id}/statut")
    public ResponseEntity<CompteDTO> changeStatut(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        log.info("PATCH /api/comptes/{}/statut", id);
        String statutStr = body.get("statut");
        Compte.StatutCompte statut = Compte.StatutCompte.valueOf(statutStr);
        CompteDTO compte = compteService.changeStatut(id, statut);
        return ResponseEntity.ok(compte);
    }
    
    @GetMapping("/{id}/solde/disponible")
    public ResponseEntity<Map<String, Object>> checkSoldeDisponible(
            @PathVariable Long id,
            @RequestParam BigDecimal montant) {
        log.info("GET /api/comptes/{}/solde/disponible?montant={}", id, montant);
        boolean disponible = compteService.isSoldeDisponible(id, montant);
        CompteDTO compte = compteService.getCompteById(id);
        
        Map<String, Object> response = Map.of(
            "disponible", disponible,
            "soldeActuel", compte.getSolde(),
            "montantDemande", montant,
            "soldeApresOperation", compte.getSolde().subtract(montant)
        );
        
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> fermerCompte(@PathVariable Long id) {
        log.info("DELETE /api/comptes/{}", id);
        compteService.fermerCompte(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        log.info("GET /api/comptes/health");
        Map<String, String> response = Map.of(
            "status", "UP",
            "service", "compte-service",
            "timestamp", java.time.LocalDateTime.now().toString()
        );
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/test")
    public ResponseEntity<Map<String, Object>> testPost(@RequestBody Map<String, Object> body) {
        log.info("POST /api/comptes/test - Body received: {}", body);
        Map<String, Object> response = Map.of(
            "message", "Test POST successful",
            "receivedBody", body,
            "timestamp", java.time.LocalDateTime.now().toString()
        );
        return ResponseEntity.ok(response);
    }
    
}
