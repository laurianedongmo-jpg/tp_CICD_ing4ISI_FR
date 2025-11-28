package com.willbank.notification.service;

import com.willbank.notification.entity.Notification;
import com.willbank.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    
    @Value("${notification.retry.maxAttempts:3}")
    private int maxAttempts;
    
    public Notification createNotification(Notification.TypeNotification type, 
                                          String destinataire, 
                                          String sujet, 
                                          String contenu,
                                          String eventType,
                                          Long entityId) {
        Notification notification = Notification.builder()
                .typeNotification(type)
                .destinataire(destinataire)
                .sujet(sujet)
                .contenu(contenu)
                .statut(Notification.StatutNotification.EN_ATTENTE)
                .eventType(eventType)
                .entityId(entityId)
                .nombreTentatives(0)
                .build();
        
        return notificationRepository.save(notification);
    }
    
    public void sendNotification(Notification notification) {
        try {
            notification.setNombreTentatives(notification.getNombreTentatives() + 1);
            
            switch (notification.getTypeNotification()) {
                case EMAIL:
                    emailService.sendEmail(
                        notification.getDestinataire(),
                        notification.getSujet(),
                        notification.getContenu()
                    );
                    break;
                    
                case PUSH:
                    log.info("Push notification not implemented yet");
                    break;
                    
                case SMS:
                    log.info("SMS notification not implemented yet");
                    break;
            }
            
            notification.setStatut(Notification.StatutNotification.ENVOYEE);
            notification.setDateEnvoi(LocalDateTime.now());
            log.info("Notification sent successfully: {}", notification.getId());
            
        } catch (Exception e) {
            log.error("Failed to send notification {}: {}", notification.getId(), e.getMessage());
            notification.setErreur(e.getMessage());
            
            if (notification.getNombreTentatives() >= maxAttempts) {
                notification.setStatut(Notification.StatutNotification.ECHEC);
                log.error("Notification {} failed after {} attempts", 
                        notification.getId(), maxAttempts);
            }
        }
        
        notificationRepository.save(notification);
    }
    
    @Transactional(readOnly = true)
    public Page<Notification> getNotificationsByDestinataire(String destinataire, Pageable pageable) {
        return notificationRepository.findByDestinataire(destinataire, pageable);
    }
    
    public void retryFailedNotifications() {
        log.info("Retrying failed notifications...");
        var failedNotifications = notificationRepository
                .findByStatutAndNombreTentativesLessThan(
                        Notification.StatutNotification.EN_ATTENTE, 
                        maxAttempts);
        
        for (Notification notification : failedNotifications) {
            sendNotification(notification);
        }
        
        log.info("Retry completed. Processed {} notifications", failedNotifications.size());
    }
}
