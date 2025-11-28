package com.willbank.notification.messaging.event;

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
public class TransactionExecutedEvent {
    private String eventType;
    private LocalDateTime timestamp;
    private Long transactionId;
    private String reference;
    private String typeTransaction;
    private Long compteSourceId;
    private Long compteDestinationId;
    private BigDecimal montant;
    private String devise;
    private String statut;
}
