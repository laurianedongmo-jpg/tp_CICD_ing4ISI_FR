package com.willbank.compte.service.impl;

import com.willbank.compte.dto.CompteDTO;
import com.willbank.compte.dto.CompteRequest;
import com.willbank.compte.dto.SoldeUpdateRequest;
import com.willbank.compte.entity.Compte;
import com.willbank.compte.exception.CompteNotFoundException;
import com.willbank.compte.exception.SoldeInsuffisantException;
import com.willbank.compte.mapper.CompteMapper;
import com.willbank.compte.messaging.CompteEventPublisher;
import com.willbank.compte.repository.CompteRepository;
import com.willbank.compte.service.CompteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CompteServiceImpl implements CompteService {
    
    private final CompteRepository compteRepository;
    private final CompteEventPublisher eventPublisher;
    private final CompteMapper compteMapper;
    
    @Override
    public CompteDTO createCompte(CompteRequest request) {
        try {
            log.info("Creating new compte for client: {}", request.getClientId());
            log.info("Request details: typeCompte={}, devise={}, decouvert={}, taux={}", 
                    request.getTypeCompte(), request.getDevise(), request.getDecouvertAutorise(), request.getTauxInteret());
            
            String numeroCompte = generateNumeroCompte();
            log.info("Generated numero compte: {}", numeroCompte);
            
            Compte compte = Compte.builder()
                    .numeroCompte(numeroCompte)
                    .clientId(request.getClientId())
                    .typeCompte(Compte.TypeCompte.valueOf(request.getTypeCompte()))
                    .devise(request.getDevise())
                    .solde(BigDecimal.ZERO)
                    .statut(Compte.StatutCompte.ACTIF)
                    .decouvertAutorise(request.getDecouvertAutorise())
                    .tauxInteret(request.getTauxInteret())
                    .build();
            
            log.info("Built compte entity: {}", compte);
            
            compte = compteRepository.save(compte);
            log.info("Compte saved with ID: {} and numero: {}", compte.getId(), compte.getNumeroCompte());
            
            // Publier l'événement
            eventPublisher.publishCompteCreated(compte);
            
            CompteDTO dto = compteMapper.toDTO(compte);
            log.info("Mapped to DTO: {}", dto);
            
            return dto;
        } catch (Exception e) {
            log.error("Error in createCompte service: ", e);
            throw e;
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public CompteDTO getCompteById(Long id) {
        log.info("Fetching compte by ID: {}", id);
        Compte compte = compteRepository.findById(id)
                .orElseThrow(() -> new CompteNotFoundException("Compte non trouvé avec l'ID: " + id));
        return compteMapper.toDTO(compte);
    }
    
    @Override
    @Transactional(readOnly = true)
    public CompteDTO getCompteByNumero(String numeroCompte) {
        log.info("Fetching compte by numero: {}", numeroCompte);
        Compte compte = compteRepository.findByNumeroCompte(numeroCompte)
                .orElseThrow(() -> new CompteNotFoundException("Compte non trouvé avec le numéro: " + numeroCompte));
        return compteMapper.toDTO(compte);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CompteDTO> getComptesByClient(Long clientId) {
        log.info("Fetching comptes for client: {}", clientId);
        return compteRepository.findByClientId(clientId)
                .stream()
                .map(compteMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public CompteDTO updateSolde(Long id, SoldeUpdateRequest request) {
        log.info("Updating solde for compte ID: {}, operation: {}, montant: {}", 
                id, request.getOperation(), request.getMontant());
        
        Compte compte = compteRepository.findById(id)
                .orElseThrow(() -> new CompteNotFoundException("Compte non trouvé avec l'ID: " + id));
        
        // Vérifier la version pour optimistic locking
        if (!compte.getVersion().equals(request.getVersion())) {
            throw new IllegalStateException("Le compte a été modifié par un autre utilisateur");
        }
        
        // Vérifier que le compte est actif
        if (compte.getStatut() != Compte.StatutCompte.ACTIF) {
            throw new IllegalStateException("Le compte n'est pas actif");
        }
        
        BigDecimal ancienSolde = compte.getSolde();
        BigDecimal nouveauSolde;
        
        if (request.getOperation() == SoldeUpdateRequest.Operation.CREDIT) {
            nouveauSolde = ancienSolde.add(request.getMontant());
        } else {
            // DEBIT
            nouveauSolde = ancienSolde.subtract(request.getMontant());
            
            // Vérifier le solde minimum (avec découvert autorisé)
            BigDecimal soldeMinimumAutorise = compte.getDecouvertAutorise().negate();
            if (nouveauSolde.compareTo(soldeMinimumAutorise) < 0) {
                throw new SoldeInsuffisantException(
                    "Solde insuffisant. Solde actuel: " + ancienSolde + 
                    ", Découvert autorisé: " + compte.getDecouvertAutorise());
            }
        }
        
        compte.setSolde(nouveauSolde);
        compte = compteRepository.save(compte);
        
        log.info("Solde updated for compte: {} (ancien: {}, nouveau: {})", 
                compte.getNumeroCompte(), ancienSolde, nouveauSolde);
        
        // Publier l'événement
        eventPublisher.publishSoldeUpdated(compte, ancienSolde, nouveauSolde, request.getMontant(), request.getOperation().name());
        
        return compteMapper.toDTO(compte);
    }
    
    @Override
    public CompteDTO changeStatut(Long id, Compte.StatutCompte statut) {
        log.info("Changing status of compte ID: {} to {}", id, statut);
        
        Compte compte = compteRepository.findById(id)
                .orElseThrow(() -> new CompteNotFoundException("Compte non trouvé avec l'ID: " + id));
        
        Compte.StatutCompte oldStatut = compte.getStatut();
        compte.setStatut(statut);
        
        if (statut == Compte.StatutCompte.FERME) {
            compte.setDateFermeture(LocalDateTime.now());
        }
        
        compte = compteRepository.save(compte);
        
        // Publier l'événement
        eventPublisher.publishCompteStatusChanged(compte, oldStatut, statut);
        
        return compteMapper.toDTO(compte);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isSoldeDisponible(Long id, BigDecimal montant) {
        Compte compte = compteRepository.findById(id)
                .orElseThrow(() -> new CompteNotFoundException("Compte non trouvé avec l'ID: " + id));
        
        if (compte.getStatut() != Compte.StatutCompte.ACTIF) {
            return false;
        }
        
        BigDecimal soldeApresOperation = compte.getSolde().subtract(montant);
        BigDecimal soldeMinimumAutorise = compte.getDecouvertAutorise().negate();
        
        return soldeApresOperation.compareTo(soldeMinimumAutorise) >= 0;
    }
    
    @Override
    public void fermerCompte(Long id) {
        log.info("Closing compte ID: {}", id);
        
        Compte compte = compteRepository.findById(id)
                .orElseThrow(() -> new CompteNotFoundException("Compte non trouvé avec l'ID: " + id));
        
        // Vérifier que le solde est à zéro
        if (compte.getSolde().compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalStateException("Le compte ne peut être fermé que si le solde est à zéro");
        }
        
        compte.setStatut(Compte.StatutCompte.FERME);
        compte.setDateFermeture(LocalDateTime.now());
        compteRepository.save(compte);
        
        log.info("Compte closed: {}", compte.getNumeroCompte());
    }
    
    private String generateNumeroCompte() {
        // Format: SN + code agence (001) + année + séquence
        String year = String.valueOf(Year.now().getValue());
        long count = compteRepository.count() + 1;
        return String.format("SN001%s%08d", year, count);
    }
}
