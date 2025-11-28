# Microservice Client - Conception détaillée

## 1. Responsabilités
- Gestion du cycle de vie des clients (CRUD)
- KYC simplifié (Know Your Customer)
- Validation des données clients
- Publication d'événements liés aux clients

## 2. Architecture logique

```
┌─────────────────────────────────────────────────────────────┐
│                    Client Service                            │
├─────────────────────────────────────────────────────────────┤
│  Controller Layer                                            │
│  ├── ClientController (REST API)                            │
│                                                              │
│  Service Layer                                               │
│  ├── ClientService (Business Logic)                         │
│  ├── KYCService (Validation KYC)                            │
│                                                              │
│  Repository Layer                                            │
│  ├── ClientRepository (JPA)                                 │
│                                                              │
│  Messaging Layer                                             │
│  ├── ClientEventPublisher (RabbitMQ)                        │
└─────────────────────────────────────────────────────────────┘
         │                                    │
         ▼                                    ▼
┌──────────────────┐              ┌──────────────────┐
│   PostgreSQL     │              │    RabbitMQ      │
│   Client DB      │              │  Event Broker    │
└──────────────────┘              └──────────────────┘
```

## 3. Modèle de données

### Table: clients

```sql
CREATE TABLE clients (
    id BIGSERIAL PRIMARY KEY,
    numero_client VARCHAR(20) UNIQUE NOT NULL,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    date_naissance DATE,
    adresse TEXT,
    telephone VARCHAR(20) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    statut VARCHAR(20) NOT NULL, -- ACTIF, INACTIF, BLOQUE, EN_ATTENTE
    type_client VARCHAR(20) NOT NULL, -- PARTICULIER, PROFESSIONNEL
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    kyc_valide BOOLEAN DEFAULT FALSE,
    kyc_date_validation TIMESTAMP,
    version INTEGER DEFAULT 0 -- Pour optimistic locking
);

CREATE INDEX idx_clients_numero ON clients(numero_client);
CREATE INDEX idx_clients_email ON clients(email);
CREATE INDEX idx_clients_telephone ON clients(telephone);
CREATE INDEX idx_clients_statut ON clients(statut);
```

### Table: documents_kyc

```sql
CREATE TABLE documents_kyc (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL REFERENCES clients(id),
    type_document VARCHAR(50) NOT NULL, -- CNI, PASSEPORT, JUSTIFICATIF_DOMICILE
    numero_document VARCHAR(50),
    date_emission DATE,
    date_expiration DATE,
    fichier_url TEXT,
    statut VARCHAR(20) NOT NULL, -- EN_ATTENTE, VALIDE, REJETE
    date_upload TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_validation TIMESTAMP
);

CREATE INDEX idx_documents_client ON documents_kyc(client_id);
```

## 4. APIs REST

### 4.1 Créer un client
```
POST /api/clients
Content-Type: application/json

Request Body:
{
    "nom": "DIALLO",
    "prenom": "Mamadou",
    "dateNaissance": "1990-05-15",
    "adresse": "Dakar, Senegal",
    "telephone": "+221771234567",
    "email": "mamadou.diallo@example.com",
    "typeClient": "PARTICULIER"
}

Response: 201 Created
{
    "id": 1,
    "numeroClient": "CLI20250001",
    "nom": "DIALLO",
    "prenom": "Mamadou",
    "dateNaissance": "1990-05-15",
    "adresse": "Dakar, Senegal",
    "telephone": "+221771234567",
    "email": "mamadou.diallo@example.com",
    "statut": "EN_ATTENTE",
    "typeClient": "PARTICULIER",
    "kycValide": false,
    "dateCreation": "2025-11-26T10:30:00Z"
}
```

### 4.2 Obtenir un client par ID
```
GET /api/clients/{id}

Response: 200 OK
{
    "id": 1,
    "numeroClient": "CLI20250001",
    "nom": "DIALLO",
    "prenom": "Mamadou",
    ...
}
```

### 4.3 Obtenir un client par numéro
```
GET /api/clients/numero/{numeroClient}

Response: 200 OK
```

### 4.4 Rechercher des clients
```
GET /api/clients/search?nom=DIALLO&statut=ACTIF&page=0&size=10

Response: 200 OK
{
    "content": [...],
    "totalElements": 25,
    "totalPages": 3,
    "number": 0,
    "size": 10
}
```

### 4.5 Mettre à jour un client
```
PUT /api/clients/{id}
Content-Type: application/json

Request Body:
{
    "adresse": "Nouvelle adresse",
    "telephone": "+221771234568",
    "version": 0
}

Response: 200 OK
```

### 4.6 Changer le statut d'un client
```
PATCH /api/clients/{id}/statut
Content-Type: application/json

Request Body:
{
    "statut": "ACTIF"
}

Response: 200 OK
```

### 4.7 Valider le KYC
```
POST /api/clients/{id}/kyc/valider

Response: 200 OK
{
    "id": 1,
    "kycValide": true,
    "kycDateValidation": "2025-11-26T11:00:00Z",
    "statut": "ACTIF"
}
```

### 4.8 Ajouter un document KYC
```
POST /api/clients/{id}/documents
Content-Type: multipart/form-data

Form Data:
- typeDocument: CNI
- numeroDocument: 1234567890
- dateEmission: 2020-01-01
- dateExpiration: 2030-01-01
- fichier: [binary file]

Response: 201 Created
```

### 4.9 Lister les clients
```
GET /api/clients?page=0&size=20&sort=dateCreation,desc

Response: 200 OK
```

### 4.10 Supprimer un client (soft delete)
```
DELETE /api/clients/{id}

Response: 204 No Content
```

## 5. Événements publiés (RabbitMQ)

### 5.1 ClientCreatedEvent
```json
{
    "eventType": "CLIENT_CREATED",
    "timestamp": "2025-11-26T10:30:00Z",
    "clientId": 1,
    "numeroClient": "CLI20250001",
    "nom": "DIALLO",
    "prenom": "Mamadou",
    "email": "mamadou.diallo@example.com",
    "telephone": "+221771234567"
}
```

### 5.2 ClientUpdatedEvent
```json
{
    "eventType": "CLIENT_UPDATED",
    "timestamp": "2025-11-26T11:00:00Z",
    "clientId": 1,
    "numeroClient": "CLI20250001",
    "changedFields": ["adresse", "telephone"]
}
```

### 5.3 ClientKYCValidatedEvent
```json
{
    "eventType": "CLIENT_KYC_VALIDATED",
    "timestamp": "2025-11-26T11:00:00Z",
    "clientId": 1,
    "numeroClient": "CLI20250001",
    "email": "mamadou.diallo@example.com"
}
```

### 5.4 ClientStatusChangedEvent
```json
{
    "eventType": "CLIENT_STATUS_CHANGED",
    "timestamp": "2025-11-26T11:30:00Z",
    "clientId": 1,
    "numeroClient": "CLI20250001",
    "oldStatus": "EN_ATTENTE",
    "newStatus": "ACTIF"
}
```

## 6. Configuration RabbitMQ

### Exchanges
- **client.events** (topic exchange)

### Routing Keys
- `client.created`
- `client.updated`
- `client.kyc.validated`
- `client.status.changed`

## 7. Règles métier

1. Le numéro client est généré automatiquement (format: CLI + année + séquence)
2. Email et téléphone doivent être uniques
3. Un client ne peut être ACTIF que si son KYC est validé
4. La modification d'un client nécessite le numéro de version (optimistic locking)
5. La suppression est logique (soft delete) - changement de statut à INACTIF

## 8. Validation des données

- Nom et prénom: obligatoires, 2-100 caractères
- Email: format valide
- Téléphone: format international (+XXX...)
- Date de naissance: client doit avoir au moins 18 ans
- Adresse: obligatoire pour validation KYC

## 9. Configuration technique

### application.yml
```yaml
server:
  port: 8081

spring:
  application:
    name: client-service
  datasource:
    url: jdbc:postgresql://localhost:5432/willbank_clients
    username: willbank
    password: ${DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```
