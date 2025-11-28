package com.willbank.composite.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDTO {
    private Long id;
    private String reference;
    private String typeTransaction;
    private Long compteSourceId;
    private Long compteDestinationId;
    private BigDecimal montant;
    private String devise;
    private String statut;
    private String description;
    private LocalDateTime dateTransaction;
    private BigDecimal frais;
}
