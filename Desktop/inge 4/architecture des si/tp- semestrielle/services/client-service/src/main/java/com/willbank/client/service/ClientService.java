package com.willbank.client.service;

import com.willbank.client.dto.ClientDTO;
import com.willbank.client.dto.ClientRequest;
import com.willbank.client.entity.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClientService {
    ClientDTO createClient(ClientRequest request);
    ClientDTO getClientById(Long id);
    ClientDTO getClientByNumero(String numeroClient);
    Page<ClientDTO> getAllClients(Pageable pageable);
    Page<ClientDTO> searchClients(String searchTerm, Pageable pageable);
    ClientDTO updateClient(Long id, ClientRequest request, Integer version);
    ClientDTO changeStatut(Long id, Client.StatutClient statut);
    ClientDTO validateKYC(Long id);
    void deleteClient(Long id);
}
