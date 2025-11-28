package com.willbank.notification.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientCreatedEvent {
    private String eventType;
    private LocalDateTime timestamp;
    private Long clientId;
    private String numeroClient;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
}
