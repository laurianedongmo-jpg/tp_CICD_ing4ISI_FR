package com.willbank.compte.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SoldeUpdateRequest {
    
    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit être positif")
    private BigDecimal montant;
    
    @NotNull(message = "L'opération est obligatoire")
    private Operation operation;
    
    @NotNull(message = "La version est obligatoire")
    private Integer version;
    
    public enum Operation {
        CREDIT, DEBIT
    }
}
