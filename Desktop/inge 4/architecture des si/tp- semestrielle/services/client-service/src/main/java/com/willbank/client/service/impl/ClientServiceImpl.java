package com.willbank.client.service.impl;

import com.willbank.client.dto.ClientDTO;
import com.willbank.client.dto.ClientRequest;
import com.willbank.client.entity.Client;
import com.willbank.client.exception.ClientNotFoundException;
import com.willbank.client.exception.DuplicateClientException;
import com.willbank.client.mapper.ClientMapper;
import com.willbank.client.messaging.ClientEventPublisher;
import com.willbank.client.repository.ClientRepository;
import com.willbank.client.service.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Year;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ClientServiceImpl implements ClientService {
    
    private final ClientRepository clientRepository;
    private final ClientEventPublisher eventPublisher;
    private final ClientMapper clientMapper;
    
    @Override
    public ClientDTO createClient(ClientRequest request) {
        log.info("Creating new client: {} {}", request.getNom(), request.getPrenom());
        
        // Vérifier l'unicité de l'email et du téléphone
        if (clientRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateClientException("Un client avec cet email existe déjà");
        }
        if (clientRepository.existsByTelephone(request.getTelephone())) {
            throw new DuplicateClientException("Un client avec ce téléphone existe déjà");
        }
        
        // Créer le client
        Client client = Client.builder()
                .numeroClient(generateNumeroClient())
                .nom(request.getNom().toUpperCase())
                .prenom(request.getPrenom())
                .dateNaissance(request.getDateNaissance())
                .adresse(request.getAdresse())
                .telephone(request.getTelephone())
                .email(request.getEmail().toLowerCase())
                .typeClient(Client.TypeClient.valueOf(request.getTypeClient()))
                .statut(Client.StatutClient.EN_ATTENTE)
                .kycValide(false)
                .build();
        
        client = clientRepository.save(client);
        log.info("Client created with ID: {} and numero: {}", client.getId(), client.getNumeroClient());
        
        // Publier l'événement
        eventPublisher.publishClientCreated(client);
        
        return clientMapper.toDTO(client);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ClientDTO getClientById(Long id) {
        log.info("Fetching client by ID: {}", id);
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException("Client non trouvé avec l'ID: " + id));
        return clientMapper.toDTO(client);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ClientDTO getClientByNumero(String numeroClient) {
        log.info("Fetching client by numero: {}", numeroClient);
        Client client = clientRepository.findByNumeroClient(numeroClient)
                .orElseThrow(() -> new ClientNotFoundException("Client non trouvé avec le numéro: " + numeroClient));
        return clientMapper.toDTO(client);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ClientDTO> getAllClients(Pageable pageable) {
        log.info("Fetching all clients, page: {}", pageable.getPageNumber());
        return clientRepository.findAll(pageable)
                .map(clientMapper::toDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ClientDTO> searchClients(String searchTerm, Pageable pageable) {
        log.info("Searching clients with term: {}", searchTerm);
        return clientRepository.searchClients(searchTerm, pageable)
                .map(clientMapper::toDTO);
    }
    
    @Override
    public ClientDTO updateClient(Long id, ClientRequest request, Integer version) {
        log.info("Updating client ID: {}", id);
        
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException("Client non trouvé avec l'ID: " + id));
        
        // Vérifier la version pour optimistic locking
        if (!client.getVersion().equals(version)) {
            throw new IllegalStateException("Le client a été modifié par un autre utilisateur");
        }
        
        // Mettre à jour les champs modifiables
        if (request.getAdresse() != null) {
            client.setAdresse(request.getAdresse());
        }
        if (request.getTelephone() != null && !request.getTelephone().equals(client.getTelephone())) {
            if (clientRepository.existsByTelephone(request.getTelephone())) {
                throw new DuplicateClientException("Ce numéro de téléphone est déjà utilisé");
            }
            client.setTelephone(request.getTelephone());
        }
        
        client = clientRepository.save(client);
        log.info("Client updated: {}", client.getId());
        
        // Publier l'événement
        eventPublisher.publishClientUpdated(client);
        
        return clientMapper.toDTO(client);
    }
    
    @Override
    public ClientDTO changeStatut(Long id, Client.StatutClient statut) {
        log.info("Changing status of client ID: {} to {}", id, statut);
        
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException("Client non trouvé avec l'ID: " + id));
        
        Client.StatutClient oldStatut = client.getStatut();
        client.setStatut(statut);
        client = clientRepository.save(client);
        
        // Publier l'événement
        eventPublisher.publishClientStatusChanged(client, oldStatut, statut);
        
        return clientMapper.toDTO(client);
    }
    
    @Override
    public ClientDTO validateKYC(Long id) {
        log.info("Validating KYC for client ID: {}", id);
        
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException("Client non trouvé avec l'ID: " + id));
        
        client.setKycValide(true);
        client.setKycDateValidation(LocalDateTime.now());
        client.setStatut(Client.StatutClient.ACTIF);
        
        client = clientRepository.save(client);
        log.info("KYC validated for client: {}", client.getNumeroClient());
        
        // Publier l'événement
        eventPublisher.publishClientKYCValidated(client);
        
        return clientMapper.toDTO(client);
    }
    
    @Override
    public void deleteClient(Long id) {
        log.info("Deleting client ID: {}", id);
        
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException("Client non trouvé avec l'ID: " + id));
        
        // Soft delete - changer le statut à INACTIF
        client.setStatut(Client.StatutClient.INACTIF);
        clientRepository.save(client);
        
        log.info("Client deleted (soft): {}", client.getNumeroClient());
    }
    
    private String generateNumeroClient() {
        // Format: CLI + année + séquence
        String year = String.valueOf(Year.now().getValue());
        long count = clientRepository.count() + 1;
        return String.format("CLI%s%05d", year, count);
    }
}
