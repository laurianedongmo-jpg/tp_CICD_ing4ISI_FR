# Microservice Transaction - Conception détaillée

## 1. Responsabilités
- Gestion des transactions bancaires
- Virements internes WillBank
- Dépôts et retraits
- Paiements marchands
- Historique des transactions
- Publication d'événements de transaction

## 2. Architecture logique

```
┌─────────────────────────────────────────────────────────────┐
│                  Transaction Service                         │
├─────────────────────────────────────────────────────────────┤
│  Controller Layer                                            │
│  ├── TransactionController (REST API)                       │
│                                                              │
│  Service Layer                                               │
│  ├── TransactionService (Business Logic)                    │
│  ├── VirementService (Virements)                            │
│  ├── ValidationService (Validation)                         │
│                                                              │
│  Repository Layer                                            │
│  ├── TransactionRepository (JPA)                            │
│                                                              │
│  Messaging Layer                                             │
│  ├── TransactionEventPublisher (RabbitMQ)                   │
│                                                              │
│  Integration Layer                                           │
│  ├── CompteServiceClient (Feign Client)                     │
└─────────────────────────────────────────────────────────────┘
         │                    │                    │
         ▼                    ▼                    ▼
┌──────────────┐   ┌──────────────┐   ┌──────────────┐
│ PostgreSQL   │   │   RabbitMQ   │   │Compte Service│
│Transaction DB│   │Event Broker  │   │   (REST)     │
└──────────────┘   └──────────────┘   └──────────────┘
```

## 3. Modèle de données

### Table: transactions

```sql
CREATE TABLE transactions (
    id BIGSERIAL PRIMARY KEY,
    reference VARCHAR(50) UNIQUE NOT NULL,
    type_transaction VARCHAR(30) NOT NULL, -- VIREMENT, DEPOT, RETRAIT, PAIEMENT
    compte_source_id BIGINT,
    compte_destination_id BIGINT,
    montant DECIMAL(15,2) NOT NULL,
    devise VARCHAR(3) DEFAULT 'XOF',
    statut VARCHAR(20) NOT NULL, -- EN_ATTENTE, VALIDEE, REJETEE, ANNULEE
    description TEXT,
    date_transaction TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_valeur DATE,
    frais DECIMAL(10,2) DEFAULT 0.00,
    motif_rejet TEXT,
    metadata JSONB, -- Données additionnelles (merchant_id, etc.)
    created_by VARCHAR(100),
    version INTEGER DEFAULT 0
);

CREATE INDEX idx_transactions_reference ON transactions(reference);
CREATE INDEX idx_transactions_compte_source ON transactions(compte_source_id);
CREATE INDEX idx_transactions_compte_dest ON transactions(compte_destination_id);
CREATE INDEX idx_transactions_date ON transactions(date_transaction);
CREATE INDEX idx_transactions_type ON transactions(type_transaction);
CREATE INDEX idx_transactions_statut ON transactions(statut);
```

## 4. APIs REST

### 4.1 Effectuer un virement interne
```
POST /api/transactions/virement
Content-Type: application/json

Request Body:
{
    "compteSourceId": 1,
    "compteDestinationId": 2,
    "montant": 50000.00,
    "devise": "XOF",
    "description": "Virement pour loyer"
}

Response: 201 Created
{
    "id": 1,
    "reference": "TRX20251126001",
    "typeTransaction": "VIREMENT",
    "compteSourceId": 1,
    "compteDestinationId": 2,
    "montant": 50000.00,
    "devise": "XOF",
    "statut": "VALIDEE",
    "description": "Virement pour loyer",
    "dateTransaction": "2025-11-26T10:30:00Z",
    "frais": 500.00
}
```

### 4.2 Effectuer un dépôt
```
POST /api/transactions/depot
Content-Type: application/json

Request Body:
{
    "compteDestinationId": 1,
    "montant": 100000.00,
    "devise": "XOF",
    "description": "Dépôt en espèces"
}

Response: 201 Created
```

### 4.3 Effectuer un retrait
```
POST /api/transactions/retrait
Content-Type: application/json

Request Body:
{
    "compteSourceId": 1,
    "montant": 25000.00,
    "devise": "XOF",
    "description": "Retrait DAB"
}

Response: 201 Created
```

### 4.4 Effectuer un paiement marchand
```
POST /api/transactions/paiement
Content-Type: application/json

Request Body:
{
    "compteSourceId": 1,
    "montant": 15000.00,
    "devise": "XOF",
    "description": "Paiement chez Auchan",
    "metadata": {
        "merchantId": "MERCH123",
        "merchantName": "Auchan Dakar"
    }
}

Response: 201 Created
```

### 4.5 Obtenir une transaction par référence
```
GET /api/transactions/reference/{reference}

Response: 200 OK
```

### 4.6 Obtenir l'historique d'un compte
```
GET /api/transactions/compte/{compteId}?page=0&size=20&sort=dateTransaction,desc

Response: 200 OK
{
    "content": [
        {
            "id": 1,
            "reference": "TRX20251126001",
            "typeTransaction": "VIREMENT",
            "montant": 50000.00,
            "statut": "VALIDEE",
            "dateTransaction": "2025-11-26T10:30:00Z"
        }
    ],
    "totalElements": 150,
    "totalPages": 8
}
```

### 4.7 Rechercher des transactions
```
GET /api/transactions/search?compteId=1&type=VIREMENT&dateDebut=2025-11-01&dateFin=2025-11-30

Response: 200 OK
```

### 4.8 Obtenir les transactions par type
```
GET /api/transactions/type/{typeTransaction}?compteId=1

Response: 200 OK
```

### 4.9 Annuler une transaction
```
POST /api/transactions/{id}/annuler
Content-Type: application/json

Request Body:
{
    "motif": "Erreur de saisie"
}

Response: 200 OK
```

### 4.10 Obtenir le relevé d'un compte
```
GET /api/transactions/releve/{compteId}?dateDebut=2025-11-01&dateFin=2025-11-30

Response: 200 OK
{
    "compteId": 1,
    "numeroCompte": "SN001202500000001",
    "periode": {
        "debut": "2025-11-01",
        "fin": "2025-11-30"
    },
    "soldeDebut": 100000.00,
    "soldeFin": 150000.00,
    "totalCredits": 200000.00,
    "totalDebits": 150000.00,
    "nombreTransactions": 45,
    "transactions": [...]
}
```

## 5. Événements publiés (RabbitMQ)

### 5.1 TransactionExecutedEvent
```json
{
    "eventType": "TRANSACTION_EXECUTED",
    "timestamp": "2025-11-26T10:30:00Z",
    "transactionId": 1,
    "reference": "TRX20251126001",
    "typeTransaction": "VIREMENT",
    "compteSourceId": 1,
    "compteDestinationId": 2,
    "montant": 50000.00,
    "devise": "XOF",
    "statut": "VALIDEE"
}
```

### 5.2 TransactionRejectedEvent
```json
{
    "eventType": "TRANSACTION_REJECTED",
    "timestamp": "2025-11-26T10:35:00Z",
    "transactionId": 2,
    "reference": "TRX20251126002",
    "compteSourceId": 1,
    "montant": 200000.00,
    "motifRejet": "Solde insuffisant"
}
```

### 5.3 TransactionCancelledEvent
```json
{
    "eventType": "TRANSACTION_CANCELLED",
    "timestamp": "2025-11-26T11:00:00Z",
    "transactionId": 1,
    "reference": "TRX20251126001",
    "motif": "Erreur de saisie"
}
```

## 6. Règles métier

1. La référence de transaction est générée automatiquement (TRX + date + séquence)
2. Validation du solde avant toute transaction de débit
3. Les virements nécessitent un compte source et destination
4. Les dépôts n'ont qu'un compte destination
5. Les retraits n'ont qu'un compte source
6. Frais appliqués selon le type de transaction
7. Une transaction VALIDEE ne peut être modifiée, seulement annulée
8. L'annulation crée une transaction inverse
9. Vérification du statut des comptes (ACTIF) avant transaction

## 7. Flux de traitement d'un virement

1. Réception de la demande de virement
2. Validation des données (montant > 0, comptes différents)
3. Appel au Compte Service pour vérifier le compte source (statut ACTIF, solde suffisant)
4. Appel au Compte Service pour vérifier le compte destination (statut ACTIF)
5. Création de la transaction avec statut EN_ATTENTE
6. Débit du compte source (appel Compte Service)
7. Crédit du compte destination (appel Compte Service)
8. Mise à jour du statut à VALIDEE
9. Publication de l'événement TransactionExecutedEvent
10. Retour de la réponse au client

## 8. Configuration technique

### application.yml
```yaml
server:
  port: 8083

spring:
  application:
    name: transaction-service
  datasource:
    url: jdbc:postgresql://localhost:5432/willbank_transactions
    username: willbank
    password: ${DB_PASSWORD}

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/

feign:
  client:
    config:
      compte-service:
        connectTimeout: 5000
        readTimeout: 5000

resilience4j:
  circuitbreaker:
    instances:
      compteService:
        slidingWindowSize: 10
        failureRateThreshold: 50
        waitDurationInOpenState: 10000
```
