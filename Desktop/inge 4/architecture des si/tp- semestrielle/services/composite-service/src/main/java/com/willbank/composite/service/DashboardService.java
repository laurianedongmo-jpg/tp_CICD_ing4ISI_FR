package com.willbank.composite.service;

import com.willbank.composite.client.ClientServiceClient;
import com.willbank.composite.client.CompteServiceClient;
import com.willbank.composite.client.TransactionServiceClient;
import com.willbank.composite.dto.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {
    
    private final ClientServiceClient clientServiceClient;
    private final CompteServiceClient compteServiceClient;
    private final TransactionServiceClient transactionServiceClient;
    
    @CircuitBreaker(name = "dashboard", fallbackMethod = "getDashboardFallback")
    @TimeLimiter(name = "dashboard")
    @Cacheable(value = "dashboard", key = "#clientId")
    public CompletableFuture<DashboardDTO> getDashboard(Long clientId) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("Building dashboard for client: {}", clientId);
            
            // Appels parallèles pour optimiser les performances
            CompletableFuture<ClientDTO> clientFuture = CompletableFuture.supplyAsync(() -> 
                clientServiceClient.getClientById(clientId));
            
            CompletableFuture<List<CompteDTO>> comptesFuture = CompletableFuture.supplyAsync(() -> 
                compteServiceClient.getComptesByClient(clientId));
            
            // Attendre les résultats
            ClientDTO client = clientFuture.join();
            List<CompteDTO> comptes = comptesFuture.join();
            
            // Récupérer les dernières transactions pour chaque compte
            List<TransactionDTO> allTransactions = new ArrayList<>();
            for (CompteDTO compte : comptes) {
                try {
                    List<TransactionDTO> transactions = transactionServiceClient
                            .getTransactionsByCompte(compte.getId());
                    allTransactions.addAll(transactions);
                } catch (Exception e) {
                    log.warn("Failed to get transactions for compte {}: {}", compte.getId(), e.getMessage());
                }
            }
            
            // Trier par date décroissante et limiter à 10
            List<TransactionDTO> dernieresTransactions = allTransactions.stream()
                    .sorted(Comparator.comparing(TransactionDTO::getDateTransaction).reversed())
                    .limit(10)
                    .collect(Collectors.toList());
            
            // Calculer les statistiques
            DashboardDTO.StatistiquesDTO statistiques = calculateStatistics(comptes, allTransactions);
            
            DashboardDTO dashboard = DashboardDTO.builder()
                    .client(client)
                    .comptes(comptes)
                    .dernieresTransactions(dernieresTransactions)
                    .statistiques(statistiques)
                    .build();
            
            log.info("Dashboard built successfully for client: {}", clientId);
            return dashboard;
        });
    }
    
    private DashboardDTO.StatistiquesDTO calculateStatistics(List<CompteDTO> comptes, 
                                                             List<TransactionDTO> transactions) {
        BigDecimal soldeTotal = comptes.stream()
                .map(CompteDTO::getSolde)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        LocalDateTime debutMois = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        long transactionsMois = transactions.stream()
                .filter(t -> t.getDateTransaction().isAfter(debutMois))
                .count();
        
        return DashboardDTO.StatistiquesDTO.builder()
                .nombreComptes(comptes.size())
                .soldeTotal(soldeTotal)
                .nombreTransactionsMois((int) transactionsMois)
                .build();
    }
    
    public CompletableFuture<DashboardDTO> getDashboardFallback(Long clientId, Exception e) {
        log.error("Circuit breaker activated for dashboard of client {}: {}", clientId, e.getMessage());
        
        DashboardDTO fallbackDashboard = DashboardDTO.builder()
                .client(null)
                .comptes(new ArrayList<>())
                .dernieresTransactions(new ArrayList<>())
                .statistiques(DashboardDTO.StatistiquesDTO.builder()
                        .nombreComptes(0)
                        .soldeTotal(BigDecimal.ZERO)
                        .nombreTransactionsMois(0)
                        .build())
                .build();
        
        return CompletableFuture.completedFuture(fallbackDashboard);
    }
}
