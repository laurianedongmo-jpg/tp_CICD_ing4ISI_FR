package com.willbank.client.mapper;

import com.willbank.client.dto.ClientDTO;
import com.willbank.client.entity.Client;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {
    
    public ClientDTO toDTO(Client client) {
        if (client == null) {
            return null;
        }
        
        return ClientDTO.builder()
                .id(client.getId())
                .numeroClient(client.getNumeroClient())
                .nom(client.getNom())
                .prenom(client.getPrenom())
                .dateNaissance(client.getDateNaissance())
                .adresse(client.getAdresse())
                .telephone(client.getTelephone())
                .email(client.getEmail())
                .statut(client.getStatut().name())
                .typeClient(client.getTypeClient().name())
                .kycValide(client.getKycValide())
                .kycDateValidation(client.getKycDateValidation())
                .dateCreation(client.getDateCreation())
                .dateModification(client.getDateModification())
                .version(client.getVersion())
                .build();
    }
}
