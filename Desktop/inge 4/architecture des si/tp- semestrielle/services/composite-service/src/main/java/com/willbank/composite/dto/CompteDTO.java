package com.willbank.composite.dto;

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
    private String statut;
    private LocalDateTime dateOuverture;
    private BigDecimal decouvertAutorise;
}
