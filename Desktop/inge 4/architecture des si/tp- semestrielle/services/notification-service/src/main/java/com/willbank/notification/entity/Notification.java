package com.willbank.notification.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type_notification", nullable = false, length = 20)
    private TypeNotification typeNotification;
    
    @Column(nullable = false, length = 200)
    private String destinataire;
    
    @Column(length = 200)
    private String sujet;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String contenu;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutNotification statut;
    
    @CreationTimestamp
    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;
    
    @Column(name = "date_envoi")
    private LocalDateTime dateEnvoi;
    
    @Column(name = "nombre_tentatives")
    private Integer nombreTentatives = 0;
    
    @Column(columnDefinition = "TEXT")
    private String erreur;
    
    @Column(name = "event_type", length = 50)
    private String eventType;
    
    @Column(name = "entity_id")
    private Long entityId;
    
    public enum TypeNotification {
        EMAIL, PUSH, SMS
    }
    
    public enum StatutNotification {
        EN_ATTENTE, ENVOYEE, ECHEC
    }
}
