# Microservice Compte - Conception détaillée

## 1. Responsabilités
- Gestion des comptes bancaires (courants, épargne)
- Gestion des soldes
- Gestion des statuts de comptes
- Validation des opérations sur comptes

## 2. Architecture logique

```
┌─────────────────────────────────────────────────────────────┐
│                    Compte Service                            │
├─────────────────────────────────────────────────────────────┤
│  Controller Layer                                            │
│  ├── CompteController (REST API)                            │
│                                                              │
│  Service Layer                                               │
│  ├── CompteService (Business Logic)                         │
│  ├── SoldeService (Gestion soldes)                          │
│                                                              │
│  Repository Layer                                            │
│  ├── CompteRepository (JPA)                                 │
│                                                              │
│  Messaging Layer                                             │
│  ├── CompteEventPublisher (RabbitMQ)                        │
└─────────────────────────────────────────────────────────────┘
         │                                    │
         ▼                                    ▼
┌──────────────────┐              ┌──────────────────┐
│   PostgreSQL     │              │    RabbitMQ      │
│   Compte DB      │              │  Event Broker    │
└──────────────────┘              └──────────────────┘
```

## 3. Modèle de données

### Table: comptes

```sql
CREATE TABLE comptes (
    id BIGSERIAL PRIMARY KEY,
    numero_compte VARCHAR(30) UNIQUE NOT NULL,
    client_id BIGINT NOT NULL,
    type_compte VARCHAR(20) NOT NULL, -- COURANT, EPARGNE
    devise VARCHAR(3) DEFAULT 'XOF',
    solde DECIMAL(15,2) DEFAULT 0.00,
    solde_minimum DECIMAL(15,2) DEFAULT 0.00,
    statut VARCHAR(20) NOT NULL, -- ACTIF, BLOQUE, FERME
    date_ouverture TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_fermeture TIMESTAMP,
    taux_interet DECIMAL(5,2), -- Pour comptes épargne
    frais_tenue_compte DECIMAL(10,2) DEFAULT 0.00,
    decouvert_autorise DECIMAL(15,2) DEFAULT 0.00,
    version INTEGER DEFAULT 0
);

CREATE INDEX idx_comptes_numero ON comptes(numero_compte);
CREATE INDEX idx_comptes_client ON comptes(client_id);
CREATE INDEX idx_comptes_statut ON comptes(statut);
```

## 4. APIs REST

### 4.1 Créer un compte
```
POST /api/comptes
Content-Type: application/json

Request Body:
{
    "clientId": 1,
    "typeCompte": "COURANT",
    "devise": "XOF",
    "decouvertAutorise": 50000.00
}

Response: 201 Created
{
    "id": 1,
    "numeroCompte": "SN001202500000001",
    "clientId": 1,
    "typeCompte": "COURANT",
    "devise": "XOF",
    "solde": 0.00,
    "statut": "ACTIF",
    "dateOuverture": "2025-11-26T10:30:00Z",
    "decouvertAutorise": 50000.00
}
```

### 4.2 Obtenir un compte par ID
```
GET /api/comptes/{id}

Response: 200 OK
```

### 4.3 Obtenir un compte par numéro
```
GET /api/comptes/numero/{numeroCompte}

Response: 200 OK
```

### 4.4 Lister les comptes d'un client
```
GET /api/comptes/client/{clientId}

Response: 200 OK
[
    {
        "id": 1,
        "numeroCompte": "SN001202500000001",
        "typeCompte": "COURANT",
        "solde": 150000.00,
        "statut": "ACTIF"
    },
    {
        "id": 2,
        "numeroCompte": "SN001202500000002",
        "typeCompte": "EPARGNE",
        "solde": 500000.00,
        "statut": "ACTIF"
    }
]
```

### 4.5 Mettre à jour le solde
```
PATCH /api/comptes/{id}/solde
Content-Type: application/json

Request Body:
{
    "montant": 10000.00,
    "operation": "CREDIT", // ou DEBIT
    "version": 0
}

Response: 200 OK
{
    "id": 1,
    "solde": 160000.00,
    "version": 1
}
```

### 4.6 Changer le statut d'un compte
```
PATCH /api/comptes/{id}/statut
Content-Type: application/json

Request Body:
{
    "statut": "BLOQUE",
    "motif": "Activité suspecte"
}

Response: 200 OK
```

### 4.7 Vérifier la disponibilité du solde
```
GET /api/comptes/{id}/solde/disponible?montant=50000

Response: 200 OK
{
    "disponible": true,
    "soldeActuel": 150000.00,
    "montantDemande": 50000.00,
    "soldeApresOperation": 100000.00
}
```

### 4.8 Fermer un compte
```
DELETE /api/comptes/{id}

Response: 204 No Content
```

## 5. Événements publiés (RabbitMQ)

### 5.1 CompteCreatedEvent
```json
{
    "eventType": "COMPTE_CREATED",
    "timestamp": "2025-11-26T10:30:00Z",
    "compteId": 1,
    "numeroCompte": "SN001202500000001",
    "clientId": 1,
    "typeCompte": "COURANT",
    "devise": "XOF"
}
```

### 5.2 SoldeUpdatedEvent
```json
{
    "eventType": "SOLDE_UPDATED",
    "timestamp": "2025-11-26T11:00:00Z",
    "compteId": 1,
    "numeroCompte": "SN001202500000001",
    "ancienSolde": 150000.00,
    "nouveauSolde": 160000.00,
    "montant": 10000.00,
    "operation": "CREDIT"
}
```

### 5.3 CompteStatusChangedEvent
```json
{
    "eventType": "COMPTE_STATUS_CHANGED",
    "timestamp": "2025-11-26T11:30:00Z",
    "compteId": 1,
    "numeroCompte": "SN001202500000001",
    "oldStatus": "ACTIF",
    "newStatus": "BLOQUE",
    "motif": "Activité suspecte"
}
```

## 6. Règles métier

1. Le numéro de compte est généré automatiquement (format: SN + code agence + année + séquence)
2. Un client peut avoir plusieurs comptes
3. Le solde ne peut être négatif sauf si découvert autorisé
4. Un compte BLOQUE ne peut effectuer aucune opération
5. Un compte FERME ne peut être réouvert
6. Les comptes épargne ont un taux d'intérêt
7. Solde minimum peut être requis selon le type de compte

## 7. Configuration technique

### application.yml
```yaml
server:
  port: 8082

spring:
  application:
    name: compte-service
  datasource:
    url: jdbc:postgresql://localhost:5432/willbank_comptes
    username: willbank
    password: ${DB_PASSWORD}

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```
