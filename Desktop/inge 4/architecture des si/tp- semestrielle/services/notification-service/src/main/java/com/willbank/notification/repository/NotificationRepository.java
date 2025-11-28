package com.willbank.notification.repository;

import com.willbank.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    Page<Notification> findByDestinataire(String destinataire, Pageable pageable);
    
    List<Notification> findByStatut(Notification.StatutNotification statut);
    
    List<Notification> findByStatutAndNombreTentativesLessThan(
            Notification.StatutNotification statut, 
            Integer maxTentatives);
    
    long countByStatutAndDateCreationBetween(
            Notification.StatutNotification statut,
            LocalDateTime dateDebut,
            LocalDateTime dateFin);
}
