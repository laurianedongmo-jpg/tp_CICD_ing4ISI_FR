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
public class CompteCreatedEvent {
    private String eventType;
    private LocalDateTime timestamp;
    private Long compteId;
    private String numeroCompte;
    private Long clientId;
    private String typeCompte;
    private String devise;
}
