package com.willbank.client.controller;

import com.willbank.client.dto.ClientDTO;
import com.willbank.client.dto.ClientRequest;
import com.willbank.client.entity.Client;
import com.willbank.client.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@Slf4j
public class ClientController {
    
    private final ClientService clientService;
    
    @PostMapping
    public ResponseEntity<ClientDTO> createClient(@Valid @RequestBody ClientRequest request) {
        log.info("POST /api/clients - Creating client: {} {}", request.getNom(), request.getPrenom());
        ClientDTO client = clientService.createClient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(client);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ClientDTO> getClientById(@PathVariable Long id) {
        log.info("GET /api/clients/{}", id);
        ClientDTO client = clientService.getClientById(id);
        return ResponseEntity.ok(client);
    }
    
    @GetMapping("/numero/{numeroClient}")
    public ResponseEntity<ClientDTO> getClientByNumero(@PathVariable String numeroClient) {
        log.info("GET /api/clients/numero/{}", numeroClient);
        ClientDTO client = clientService.getClientByNumero(numeroClient);
        return ResponseEntity.ok(client);
    }
    
    @GetMapping
    public ResponseEntity<Page<ClientDTO>> getAllClients(Pageable pageable) {
        log.info("GET /api/clients - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<ClientDTO> clients = clientService.getAllClients(pageable);
        return ResponseEntity.ok(clients);
    }
    
    @GetMapping("/search")
    public ResponseEntity<Page<ClientDTO>> searchClients(
            @RequestParam String term,
            Pageable pageable) {
        log.info("GET /api/clients/search?term={}", term);
        Page<ClientDTO> clients = clientService.searchClients(term, pageable);
        return ResponseEntity.ok(clients);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ClientDTO> updateClient(
            @PathVariable Long id,
            @Valid @RequestBody ClientRequest request,
            @RequestParam Integer version) {
        log.info("PUT /api/clients/{} - version: {}", id, version);
        ClientDTO client = clientService.updateClient(id, request, version);
        return ResponseEntity.ok(client);
    }
    
    @PatchMapping("/{id}/statut")
    public ResponseEntity<ClientDTO> changeStatut(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        log.info("PATCH /api/clients/{}/statut", id);
        String statutStr = body.get("statut");
        Client.StatutClient statut = Client.StatutClient.valueOf(statutStr);
        ClientDTO client = clientService.changeStatut(id, statut);
        return ResponseEntity.ok(client);
    }
    
    @PostMapping("/{id}/kyc/valider")
    public ResponseEntity<ClientDTO> validateKYC(@PathVariable Long id) {
        log.info("POST /api/clients/{}/kyc/valider", id);
        ClientDTO client = clientService.validateKYC(id);
        return ResponseEntity.ok(client);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        log.info("DELETE /api/clients/{}", id);
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }
}
