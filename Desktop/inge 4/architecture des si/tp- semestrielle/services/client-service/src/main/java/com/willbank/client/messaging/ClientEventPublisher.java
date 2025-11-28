package com.willbank.client.messaging;

import com.willbank.client.config.RabbitMQConfig;
import com.willbank.client.entity.Client;
import com.willbank.client.messaging.event.ClientCreatedEvent;
import com.willbank.client.messaging.event.ClientKYCValidatedEvent;
import com.willbank.client.messaging.event.ClientStatusChangedEvent;
import com.willbank.client.messaging.event.ClientUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class ClientEventPublisher {
    
    private final RabbitTemplate rabbitTemplate;
    
    public ClientEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
    
    public void publishClientCreated(Client client) {
        try {
            ClientCreatedEvent event = ClientCreatedEvent.builder()
                    .eventType("CLIENT_CREATED")
                    .timestamp(LocalDateTime.now())
                    .clientId(client.getId())
                    .numeroClient(client.getNumeroClient())
                    .nom(client.getNom())
                    .prenom(client.getPrenom())
                    .email(client.getEmail())
                    .telephone(client.getTelephone())
                    .build();
            
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.CLIENT_EXCHANGE,
                    RabbitMQConfig.CLIENT_CREATED_ROUTING_KEY,
                    event
            );
            
            log.info("Published CLIENT_CREATED event for client: {}", client.getNumeroClient());
        } catch (Exception e) {
            log.warn("Failed to publish CLIENT_CREATED event (RabbitMQ may not be available): {}", e.getMessage());
        }
    }
    
    public void publishClientUpdated(Client client) {
        try {
            ClientUpdatedEvent event = ClientUpdatedEvent.builder()
                    .eventType("CLIENT_UPDATED")
                    .timestamp(LocalDateTime.now())
                    .clientId(client.getId())
                    .numeroClient(client.getNumeroClient())
                    .build();
            
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.CLIENT_EXCHANGE,
                    RabbitMQConfig.CLIENT_UPDATED_ROUTING_KEY,
                    event
            );
            
            log.info("Published CLIENT_UPDATED event for client: {}", client.getNumeroClient());
        } catch (Exception e) {
            log.warn("Failed to publish CLIENT_UPDATED event: {}", e.getMessage());
        }
    }
    
    public void publishClientKYCValidated(Client client) {
        try {
            ClientKYCValidatedEvent event = ClientKYCValidatedEvent.builder()
                    .eventType("CLIENT_KYC_VALIDATED")
                    .timestamp(LocalDateTime.now())
                    .clientId(client.getId())
                    .numeroClient(client.getNumeroClient())
                    .email(client.getEmail())
                    .build();
            
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.CLIENT_EXCHANGE,
                    RabbitMQConfig.CLIENT_KYC_VALIDATED_ROUTING_KEY,
                    event
            );
            
            log.info("Published CLIENT_KYC_VALIDATED event for client: {}", client.getNumeroClient());
        } catch (Exception e) {
            log.warn("Failed to publish CLIENT_KYC_VALIDATED event: {}", e.getMessage());
        }
    }
    
    public void publishClientStatusChanged(Client client, Client.StatutClient oldStatus, Client.StatutClient newStatus) {
        try {
            ClientStatusChangedEvent event = ClientStatusChangedEvent.builder()
                    .eventType("CLIENT_STATUS_CHANGED")
                    .timestamp(LocalDateTime.now())
                    .clientId(client.getId())
                    .numeroClient(client.getNumeroClient())
                    .oldStatus(oldStatus.name())
                    .newStatus(newStatus.name())
                    .build();
            
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.CLIENT_EXCHANGE,
                    RabbitMQConfig.CLIENT_STATUS_CHANGED_ROUTING_KEY,
                    event
            );
            
            log.info("Published CLIENT_STATUS_CHANGED event for client: {} ({} -> {})", 
                    client.getNumeroClient(), oldStatus, newStatus);
        } catch (Exception e) {
            log.warn("Failed to publish CLIENT_STATUS_CHANGED event: {}", e.getMessage());
        }
    }
}
