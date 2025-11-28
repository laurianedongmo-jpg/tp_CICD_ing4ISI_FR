package com.willbank.client.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "clients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "numero_client", unique = true, nullable = false, length = 20)
    private String numeroClient;
    
    @Column(nullable = false, length = 100)
    private String nom;
    
    @Column(nullable = false, length = 100)
    private String prenom;
    
    @Column(name = "date_naissance")
    private LocalDate dateNaissance;
    
    @Column(columnDefinition = "TEXT")
    private String adresse;
    
    @Column(nullable = false, length = 20)
    private String telephone;
    
    @Column(unique = true, nullable = false, length = 100)
    private String email;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutClient statut;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type_client", nullable = false, length = 20)
    private TypeClient typeClient;
    
    @CreationTimestamp
    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;
    
    @UpdateTimestamp
    @Column(name = "date_modification")
    private LocalDateTime dateModification;
    
    @Column(name = "kyc_valide")
    private Boolean kycValide = false;
    
    @Column(name = "kyc_date_validation")
    private LocalDateTime kycDateValidation;
    
    @Version
    private Integer version = 0;
    
    public enum StatutClient {
        ACTIF, INACTIF, BLOQUE, EN_ATTENTE
    }
    
    public enum TypeClient {
        PARTICULIER, PROFESSIONNEL
    }
}
