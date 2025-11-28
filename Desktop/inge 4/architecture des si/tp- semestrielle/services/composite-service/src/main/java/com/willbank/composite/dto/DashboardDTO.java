package com.willbank.composite.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardDTO {
    private ClientDTO client;
    private List<CompteDTO> comptes;
    private List<TransactionDTO> dernieresTransactions;
    private StatistiquesDTO statistiques;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatistiquesDTO {
        private Integer nombreComptes;
        private BigDecimal soldeTotal;
        private Integer nombreTransactionsMois;
    }
}
