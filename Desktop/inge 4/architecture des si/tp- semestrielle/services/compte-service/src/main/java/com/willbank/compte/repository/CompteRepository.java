package com.willbank.compte.repository;

import com.willbank.compte.entity.Compte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompteRepository extends JpaRepository<Compte, Long> {
    
    Optional<Compte> findByNumeroCompte(String numeroCompte);
    
    List<Compte> findByClientId(Long clientId);
    
    List<Compte> findByClientIdAndStatut(Long clientId, Compte.StatutCompte statut);
    
    boolean existsByNumeroCompte(String numeroCompte);
    
    long countByClientId(Long clientId);
}
