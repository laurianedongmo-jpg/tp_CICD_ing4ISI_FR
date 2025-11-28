package com.willbank.compte.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompteDTO {
    private Long id;
    private String numeroCompte;
    private Long clientId;
    private String typeCompte;
    private String devise;
    private BigDecimal solde;
    private BigDecimal soldeMinimum;
    private String statut;
    private LocalDateTime dateOuverture;
    private LocalDateTime dateFermeture;
    private BigDecimal tauxInteret;
    private BigDecimal fraisTenueCompte;
    private BigDecimal decouvertAutorise;
    private Integer version;
}
