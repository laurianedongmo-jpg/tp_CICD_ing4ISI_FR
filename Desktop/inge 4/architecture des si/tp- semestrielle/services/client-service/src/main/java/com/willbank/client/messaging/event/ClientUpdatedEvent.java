package com.willbank.client.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientUpdatedEvent {
    private String eventType;
    private LocalDateTime timestamp;
    private Long clientId;
    private String numeroClient;
}
