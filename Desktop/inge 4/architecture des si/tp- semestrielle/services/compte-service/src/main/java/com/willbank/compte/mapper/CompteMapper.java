package com.willbank.compte.mapper;

import com.willbank.compte.dto.CompteDTO;
import com.willbank.compte.entity.Compte;
import org.springframework.stereotype.Component;

@Component
public class CompteMapper {
    
    public CompteDTO toDTO(Compte compte) {
        if (compte == null) {
            return null;
        }
        
        return CompteDTO.builder()
                .id(compte.getId())
                .numeroCompte(compte.getNumeroCompte())
                .clientId(compte.getClientId())
                .typeCompte(compte.getTypeCompte().name())
                .devise(compte.getDevise())
                .solde(compte.getSolde())
                .soldeMinimum(compte.getSoldeMinimum())
                .statut(compte.getStatut().name())
                .dateOuverture(compte.getDateOuverture())
                .dateFermeture(compte.getDateFermeture())
                .tauxInteret(compte.getTauxInteret())
                .fraisTenueCompte(compte.getFraisTenueCompte())
                .decouvertAutorise(compte.getDecouvertAutorise())
                .version(compte.getVersion())
                .build();
    }
}
