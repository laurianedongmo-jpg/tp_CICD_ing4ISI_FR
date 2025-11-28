package com.willbank.compte.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompteStatusChangedEvent {
    private String eventType;
    private LocalDateTime timestamp;
    private Long compteId;
    private String numeroCompte;
    private String oldStatus;
    private String newStatus;
    private String motif;
}
