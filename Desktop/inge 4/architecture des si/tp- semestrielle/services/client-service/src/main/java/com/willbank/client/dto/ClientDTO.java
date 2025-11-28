package com.willbank.client.dto;

import com.willbank.client.entity.Client;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientDTO {
    private Long id;
    private String numeroClient;
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private String adresse;
    private String telephone;
    private String email;
    private String statut;
    private String typeClient;
    private Boolean kycValide;
    private LocalDateTime kycDateValidation;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    private Integer version;
}
