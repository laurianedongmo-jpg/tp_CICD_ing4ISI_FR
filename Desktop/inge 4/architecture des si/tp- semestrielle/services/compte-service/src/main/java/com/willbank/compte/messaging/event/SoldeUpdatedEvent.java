package com.willbank.compte.messaging.event;

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
public class SoldeUpdatedEvent {
    private String eventType;
    private LocalDateTime timestamp;
    private Long compteId;
    private String numeroCompte;
    private BigDecimal ancienSolde;
    private BigDecimal nouveauSolde;
    private BigDecimal montant;
    private String operation;
}
