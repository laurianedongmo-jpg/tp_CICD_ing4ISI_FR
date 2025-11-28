package com.willbank.compte.service;

import com.willbank.compte.dto.CompteDTO;
import com.willbank.compte.dto.CompteRequest;
import com.willbank.compte.dto.SoldeUpdateRequest;
import com.willbank.compte.entity.Compte;

import java.math.BigDecimal;
import java.util.List;

public interface CompteService {
    CompteDTO createCompte(CompteRequest request);
    CompteDTO getCompteById(Long id);
    CompteDTO getCompteByNumero(String numeroCompte);
    List<CompteDTO> getComptesByClient(Long clientId);
    CompteDTO updateSolde(Long id, SoldeUpdateRequest request);
    CompteDTO changeStatut(Long id, Compte.StatutCompte statut);
    boolean isSoldeDisponible(Long id, BigDecimal montant);
    void fermerCompte(Long id);
}
