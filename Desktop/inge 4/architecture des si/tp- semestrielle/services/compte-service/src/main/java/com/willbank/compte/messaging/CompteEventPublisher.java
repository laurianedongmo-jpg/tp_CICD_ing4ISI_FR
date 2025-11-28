package com.willbank.compte.messaging;

import com.willbank.compte.entity.Compte;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Slf4j
public class CompteEventPublisher {
    
    public void publishCompteCreated(Compte compte) {
        log.info("Event: COMPTE_CREATED for compte: {} (client: {})", 
                compte.getNumeroCompte(), compte.getClientId());
    }
    
    public void publishSoldeUpdated(Compte compte, BigDecimal ancienSolde, BigDecimal nouveauSolde, 
                                    BigDecimal montant, String operation) {
        log.info("Event: SOLDE_UPDATED for compte: {} ({} -> {}, operation: {})", 
                compte.getNumeroCompte(), ancienSolde, nouveauSolde, operation);
    }
    
    public void publishCompteStatusChanged(Compte compte, Compte.StatutCompte oldStatus, 
                                          Compte.StatutCompte newStatus) {
        log.info("Event: COMPTE_STATUS_CHANGED for compte: {} ({} -> {})", 
                compte.getNumeroCompte(), oldStatus, newStatus);
    }
}
