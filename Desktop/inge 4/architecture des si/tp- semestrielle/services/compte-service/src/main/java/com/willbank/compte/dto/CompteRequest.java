package com.willbank.compte.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public class CompteRequest {
    
    @NotNull(message = "L'ID du client est obligatoire")
    private Long clientId;
    
    @NotBlank(message = "Le type de compte est obligatoire")
    private String typeCompte;
    
    private String devise = "XOF";
    
    @PositiveOrZero(message = "Le découvert autorisé doit être positif ou zéro")
    private BigDecimal decouvertAutorise = BigDecimal.ZERO;
    
    @PositiveOrZero(message = "Le taux d'intérêt doit être positif ou zéro")
    private BigDecimal tauxInteret;
    
    // Constructeurs
    public CompteRequest() {}
    
    public CompteRequest(Long clientId, String typeCompte, String devise, 
                        BigDecimal decouvertAutorise, BigDecimal tauxInteret) {
        this.clientId = clientId;
        this.typeCompte = typeCompte;
        this.devise = devise;
        this.decouvertAutorise = decouvertAutorise;
        this.tauxInteret = tauxInteret;
    }
    
    // Getters et Setters
    public Long getClientId() {
        return clientId;
    }
    
    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
    
    public String getTypeCompte() {
        return typeCompte;
    }
    
    public void setTypeCompte(String typeCompte) {
        this.typeCompte = typeCompte;
    }
    
    public String getDevise() {
        return devise;
    }
    
    public void setDevise(String devise) {
        this.devise = devise;
    }
    
    public BigDecimal getDecouvertAutorise() {
        return decouvertAutorise;
    }
    
    public void setDecouvertAutorise(BigDecimal decouvertAutorise) {
        this.decouvertAutorise = decouvertAutorise;
    }
    
    public BigDecimal getTauxInteret() {
        return tauxInteret;
    }
    
    public void setTauxInteret(BigDecimal tauxInteret) {
        this.tauxInteret = tauxInteret;
    }
    
    @Override
    public String toString() {
        return "CompteRequest{" +
                "clientId=" + clientId +
                ", typeCompte='" + typeCompte + '\'' +
                ", devise='" + devise + '\'' +
                ", decouvertAutorise=" + decouvertAutorise +
                ", tauxInteret=" + tauxInteret +
                '}';
    }
}
