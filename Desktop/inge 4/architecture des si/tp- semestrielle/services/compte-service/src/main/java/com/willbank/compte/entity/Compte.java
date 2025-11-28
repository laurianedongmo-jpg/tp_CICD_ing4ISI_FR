package com.willbank.compte.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "comptes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Compte {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "numero_compte", unique = true, nullable = false, length = 30)
    private String numeroCompte;
    
    @Column(name = "client_id", nullable = false)
    private Long clientId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type_compte", nullable = false, length = 20)
    private TypeCompte typeCompte;
    
    @Column(length = 3)
    private String devise = "XOF";
    
    @Column(precision = 15, scale = 2)
    private BigDecimal solde = BigDecimal.ZERO;
    
    @Column(name = "solde_minimum", precision = 15, scale = 2)
    private BigDecimal soldeMinimum = BigDecimal.ZERO;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutCompte statut;
    
    @CreationTimestamp
    @Column(name = "date_ouverture", updatable = false)
    private LocalDateTime dateOuverture;
    
    @Column(name = "date_fermeture")
    private LocalDateTime dateFermeture;
    
    @Column(name = "taux_interet", precision = 5, scale = 2)
    private BigDecimal tauxInteret;
    
    @Column(name = "frais_tenue_compte", precision = 10, scale = 2)
    private BigDecimal fraisTenueCompte = BigDecimal.ZERO;
    
    @Column(name = "decouvert_autorise", precision = 15, scale = 2)
    private BigDecimal decouvertAutorise = BigDecimal.ZERO;
    
    @Version
    private Integer version = 0;
    
    public enum TypeCompte {
        COURANT, EPARGNE
    }
    
    public enum StatutCompte {
        ACTIF, BLOQUE, FERME
    }
}
