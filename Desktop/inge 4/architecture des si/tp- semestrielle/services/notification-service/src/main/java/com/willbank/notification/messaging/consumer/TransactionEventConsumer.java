package com.willbank.notification.messaging.consumer;

import com.willbank.notification.entity.Notification;
import com.willbank.notification.messaging.event.TransactionExecutedEvent;
import com.willbank.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionEventConsumer {
    
    private final NotificationService notificationService;
    
    @RabbitListener(queues = "notification.transaction.executed")
    public void handleTransactionExecuted(TransactionExecutedEvent event) {
        log.info("Received TRANSACTION_EXECUTED event: {}", event.getReference());
        
        try {
            // Pour simplifier, on envoie une notification générique
            // Dans un vrai système, on récupérerait l'email du client via un appel au Client Service
            String destinataire = "client@example.com"; // À remplacer par l'email réel
            
            String sujet = "Transaction effectuée - " + event.getReference();
            String contenu = String.format(
                "Transaction %s de %s XOF effectuée avec succès.\nRéférence: %s",
                event.getTypeTransaction(),
                event.getMontant(),
                event.getReference()
            );
            
            Notification notification = notificationService.createNotification(
                Notification.TypeNotification.EMAIL,
                destinataire,
                sujet,
                contenu,
                event.getEventType(),
                event.getTransactionId()
            );
            
            notificationService.sendNotification(notification);
            
            log.info("Transaction notification sent for: {}", event.getReference());
            
        } catch (Exception e) {
            log.error("Error handling TRANSACTION_EXECUTED event: {}", e.getMessage());
        }
    }
}
