package com.willbank.composite.client;

import com.willbank.composite.dto.CompteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "compte-service")
public interface CompteServiceClient {
    
    @GetMapping("/api/comptes/client/{clientId}")
    List<CompteDTO> getComptesByClient(@PathVariable("clientId") Long clientId);
    
    @GetMapping("/api/comptes/{id}")
    CompteDTO getCompteById(@PathVariable("id") Long id);
}
