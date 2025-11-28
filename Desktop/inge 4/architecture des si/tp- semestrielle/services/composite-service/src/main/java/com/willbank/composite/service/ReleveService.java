package com.willbank.composite.service;

import com.willbank.composite.client.CompteServiceClient;
import com.willbank.composite.client.TransactionServiceClient;
import com.willbank.composite.dto.CompteDTO;
import com.willbank.composite.dto.TransactionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReleveService {
    
    private final CompteServiceClient compteServiceClient;
    private final TransactionServiceClient transactionServiceClient;
    
    @Cacheable(value = "releve", key = "#compteId + '-' + #dateDebut + '-' + #dateFin")
    public Map<String, Object> getReleve(Long compteId, LocalDateTime dateDebut, LocalDateTime dateFin) {
        log.info("Generating releve for compte: {} from {} to {}", compteId, dateDebut, dateFin);
        
        // Récupérer le compte
        CompteDTO compte = compteServiceClient.getCompteById(compteId);
        
        // Récupérer les transactions
        List<TransactionDTO> transactions = transactionServiceClient.getReleve(compteId, dateDebut, dateFin);
        
        // Calculer les statistiques
        BigDecimal totalCredits = BigDecimal.ZERO;
        BigDecimal totalDebits = BigDecimal.ZERO;
        int nombreCredits = 0;
        int nombreDebits = 0;
        
        for (TransactionDTO transaction : transactions) {
            if (transaction.getCompteDestinationId() != null && 
                transaction.getCompteDestinationId().equals(compteId)) {
                // Crédit
                totalCredits = totalCredits.add(transaction.getMontant());
                nombreCredits++;
            }
            if (transaction.getCompteSourceId() != null && 
                transaction.getCompteSourceId().equals(compteId)) {
                // Débit
                totalDebits = totalDebits.add(transaction.getMontant());
                if (transaction.getFrais() != null) {
                    totalDebits = totalDebits.add(transaction.getFrais());
                }
                nombreDebits++;
            }
        }
        
        // Construire le relevé
        Map<String, Object> releve = new HashMap<>();
        releve.put("compte", compte);
        releve.put("periode", Map.of(
            "debut", dateDebut,
            "fin", dateFin
        ));
        releve.put("soldeActuel", compte.getSolde());
        releve.put("mouvements", Map.of(
            "totalCredits", totalCredits,
            "totalDebits", totalDebits,
            "nombreCredits", nombreCredits,
            "nombreDebits", nombreDebits
        ));
        releve.put("transactions", transactions);
        releve.put("nombreTransactions", transactions.size());
        
        log.info("Releve generated successfully for compte: {}", compteId);
        return releve;
    }
    
    public Map<String, Object> getCompteOverview(Long clientId) {
        log.info("Getting compte overview for client: {}", clientId);
        
        List<CompteDTO> comptes = compteServiceClient.getComptesByClient(clientId);
        
        BigDecimal soldeTotal = comptes.stream()
                .map(CompteDTO::getSolde)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        int nombreComptesActifs = (int) comptes.stream()
                .filter(c -> "ACTIF".equals(c.getStatut()))
                .count();
        
        Map<String, Object> overview = new HashMap<>();
        overview.put("clientId", clientId);
        overview.put("comptes", comptes);
        overview.put("resume", Map.of(
            "soldeTotal", soldeTotal,
            "nombreComptes", comptes.size(),
            "nombreComptesActifs", nombreComptesActifs
        ));
        
        return overview;
    }
}
