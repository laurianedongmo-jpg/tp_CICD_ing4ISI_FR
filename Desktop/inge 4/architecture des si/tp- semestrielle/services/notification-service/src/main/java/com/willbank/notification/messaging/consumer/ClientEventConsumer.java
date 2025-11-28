package com.willbank.notification.messaging.consumer;

import com.willbank.notification.entity.Notification;
import com.willbank.notification.messaging.event.ClientCreatedEvent;
import com.willbank.notification.messaging.event.ClientKYCValidatedEvent;
import com.willbank.notification.service.EmailService;
import com.willbank.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClientEventConsumer {
    
    private final NotificationService notificationService;
    private final EmailService emailService;
    
    @RabbitListener(queues = "notification.client.created")
    public void handleClientCreated(ClientCreatedEvent event) {
        log.info("Received CLIENT_CREATED event for client: {}", event.getNumeroClient());
        
        try {
            // Créer la notification
            Notification notification = notificationService.createNotification(
                Notification.TypeNotification.EMAIL,
                event.getEmail(),
                "Bienvenue chez WillBank !",
                String.format("Bienvenue %s %s", event.getPrenom(), event.getNom()),
                event.getEventType(),
                event.getClientId()
            );
            
            // Envoyer l'email de bienvenue
            emailService.sendWelcomeEmail(
                event.getEmail(),
                event.getNom(),
                event.getPrenom(),
                event.getNumeroClient()
            );
            
            // Mettre à jour le statut
            notificationService.sendNotification(notification);
            
            log.info("Welcome email sent to: {}", event.getEmail());
            
        } catch (Exception e) {
            log.error("Error handling CLIENT_CREATED event: {}", e.getMessage());
        }
    }
    
    @RabbitListener(queues = "notification.client.kyc.validated")
    public void handleClientKYCValidated(ClientKYCValidatedEvent event) {
        log.info("Received CLIENT_KYC_VALIDATED event for client: {}", event.getNumeroClient());
        
        try {
            // Créer la notification
            Notification notification = notificationService.createNotification(
                Notification.TypeNotification.EMAIL,
                event.getEmail(),
                "KYC Validé - WillBank",
                "Votre KYC a été validé",
                event.getEventType(),
                event.getClientId()
            );
            
            // Envoyer l'email
            emailService.sendKYCValidatedEmail(
                event.getEmail(),
                event.getNumeroClient()
            );
            
            // Mettre à jour le statut
            notificationService.sendNotification(notification);
            
            log.info("KYC validation email sent to: {}", event.getEmail());
            
        } catch (Exception e) {
            log.error("Error handling CLIENT_KYC_VALIDATED event: {}", e.getMessage());
        }
    }
}
