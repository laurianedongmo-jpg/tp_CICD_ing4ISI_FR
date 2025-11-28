# Microservice Composite - Conception détaillée

## 1. Responsabilités
- Agrégation de données de plusieurs microservices
- Exposition d'APIs orientées business
- Dashboard client (comptes + soldes + transactions)
- Relevés de compte sur période
- Recherche et filtrage avancés

## 2. Architecture logique

```
┌─────────────────────────────────────────────────────────────┐
│                  Composite Service                           │
├─────────────────────────────────────────────────────────────┤
│  Controller Layer                                            │
│  ├── DashboardController                                    │
│  ├── ReleveController                                       │
│  ├── SearchController                                       │
│                                                              │
│  Service Layer                                               │
│  ├── DashboardService (Agrégation)                         │
│  ├── ReleveService (Relevés)                               │
│  ├── SearchService (Recherche)                             │
│                                                              │
│  Integration Layer (Feign Clients)                          │
│  ├── ClientServiceClient                                    │
│  ├── CompteServiceClient                                    │
│  ├── TransactionServiceClient                              │
└─────────────────────────────────────────────────────────────┘
         │                    │                    │
         ▼                    ▼                    ▼
┌──────────────┐   ┌──────────────┐   ┌──────────────┐
│   Client     │   │    Compte    │   │ Transaction  │
│   Service    │   │   Service    │   │   Service    │
└──────────────┘   └──────────────┘   └──────────────┘
```

## 3. APIs REST

### 3.1 Dashboard client complet
```
GET /api/composite/dashboard/{clientId}

Response: 200 OK
{
    "client": {
        "id": 1,
        "numeroClient": "CLI20250001",
        "nom": "DIALLO",
        "prenom": "Mamadou",
        "email": "mamadou.diallo@example.com",
        "statut": "ACTIF"
    },
    "comptes": [
        {
            "id": 1,
            "numeroCompte": "SN001202500000001",
            "typeCompte": "COURANT",
            "solde": 150000.00,
            "devise": "XOF",
            "statut": "ACTIF"
        },
        {
            "id": 2,
            "numeroCompte": "SN001202500000002",
            "typeCompte": "EPARGNE",
            "solde": 500000.00,
            "devise": "XOF",
            "statut": "ACTIF"
        }
    ],
    "dernieresTransactions": [
        {
            "id": 45,
            "reference": "TRX20251126045",
            "typeTransaction": "VIREMENT",
            "montant": 50000.00,
            "dateTransaction": "2025-11-26T10:30:00Z",
            "statut": "VALIDEE"
        },
        {
            "id": 44,
            "reference": "TRX20251125044",
            "typeTransaction": "DEPOT",
            "montant": 100000.00,
            "dateTransaction": "2025-11-25T15:20:00Z",
            "statut": "VALIDEE"
        }
    ],
    "statistiques": {
        "nombreComptes": 2,
        "soldeTotal": 650000.00,
        "nombreTransactionsMois": 28
    }
}
```

### 3.2 Relevé détaillé d'un compte
```
GET /api/composite/releve/{compteId}?dateDebut=2025-11-01&dateFin=2025-11-30

Response: 200 OK
{
    "compte": {
        "id": 1,
        "numeroCompte": "SN001202500000001",
        "typeCompte": "COURANT",
        "titulaire": {
            "nom": "DIALLO",
            "prenom": "Mamadou"
        }
    },
    "periode": {
        "debut": "2025-11-01",
        "fin": "2025-11-30"
    },
    "soldes": {
        "debut": 100000.00,
        "fin": 150000.00,
        "moyen": 125000.00
    },
    "mouvements": {
        "totalCredits": 200000.00,
        "totalDebits": 150000.00,
        "nombreCredits": 15,
        "nombreDebits": 13
    },
    "transactions": [
        {
            "date": "2025-11-26",
            "reference": "TRX20251126045",
            "type": "VIREMENT",
            "description": "Virement reçu",
            "debit": null,
            "credit": 50000.00,
            "solde": 150000.00
        },
        {
            "date": "2025-11-25",
            "reference": "TRX20251125044",
            "type": "RETRAIT",
            "description": "Retrait DAB",
            "debit": 25000.00,
            "credit": null,
            "solde": 100000.00
        }
    ]
}
```

### 3.3 Recherche de transactions par type
```
GET /api/composite/transactions/search?clientId=1&type=PAIEMENT&dateDebut=2025-11-01

Response: 200 OK
{
    "criteres": {
        "clientId": 1,
        "type": "PAIEMENT",
        "dateDebut": "2025-11-01"
    },
    "resultats": [
        {
            "transaction": {
                "id": 30,
                "reference": "TRX20251120030",
                "montant": 15000.00,
                "dateTransaction": "2025-11-20T14:30:00Z"
            },
            "compte": {
                "numeroCompte": "SN001202500000001",
                "typeCompte": "COURANT"
            },
            "details": {
                "merchantName": "Auchan Dakar",
                "merchantId": "MERCH123"
            }
        }
    ],
    "totalResultats": 8,
    "montantTotal": 120000.00
}
```

### 3.4 Vue d'ensemble multi-comptes
```
GET /api/composite/comptes/{clientId}/overview

Response: 200 OK
{
    "clientId": 1,
    "comptes": [
        {
            "compte": {
                "id": 1,
                "numeroCompte": "SN001202500000001",
                "typeCompte": "COURANT",
                "solde": 150000.00
            },
            "derniereTransaction": {
                "reference": "TRX20251126045",
                "date": "2025-11-26T10:30:00Z",
                "montant": 50000.00
            },
            "statistiquesMois": {
                "nombreTransactions": 18,
                "totalCredits": 120000.00,
                "totalDebits": 80000.00
            }
        }
    ],
    "resume": {
        "soldeTotal": 650000.00,
        "nombreComptesActifs": 2,
        "derniereActivite": "2025-11-26T10:30:00Z"
    }
}
```

### 3.5 Historique complet client
```
GET /api/composite/historique/{clientId}?dateDebut=2025-01-01&dateFin=2025-12-31

Response: 200 OK
{
    "client": {...},
    "periode": {...},
    "tousLesComptes": [
        {
            "compte": {...},
            "transactions": [...]
        }
    ],
    "statistiquesGlobales": {
        "nombreTotalTransactions": 245,
        "volumeTotal": 5000000.00,
        "moyenneParTransaction": 20408.16
    }
}
```

### 3.6 Recherche avancée
```
POST /api/composite/search/advanced
Content-Type: application/json

Request Body:
{
    "clientId": 1,
    "compteIds": [1, 2],
    "typesTransaction": ["VIREMENT", "PAIEMENT"],
    "montantMin": 10000.00,
    "montantMax": 100000.00,
    "dateDebut": "2025-11-01",
    "dateFin": "2025-11-30",
    "statuts": ["VALIDEE"]
}

Response: 200 OK
```

## 4. Logique d'agrégation

### 4.1 Dashboard Service
```java
@Service
public class DashboardService {
    
    @Autowired
    private ClientServiceClient clientService;
    
    @Autowired
    private CompteServiceClient compteService;
    
    @Autowired
    private TransactionServiceClient transactionService;
    
    @CircuitBreaker(name = "dashboard", fallbackMethod = "getDashboardFallback")
    public DashboardDTO getDashboard(Long clientId) {
        // Appels parallèles pour optimiser les performances
        CompletableFuture<ClientDTO> clientFuture = 
            CompletableFuture.supplyAsync(() -> 
                clientService.getClientById(clientId));
        
        CompletableFuture<List<CompteDTO>> comptesFuture = 
            CompletableFuture.supplyAsync(() -> 
                compteService.getComptesByClient(clientId));
        
        // Attendre les résultats
        ClientDTO client = clientFuture.join();
        List<CompteDTO> comptes = comptesFuture.join();
        
        // Récupérer les dernières transactions pour chaque compte
        List<TransactionDTO> transactions = new ArrayList<>();
        for (CompteDTO compte : comptes) {
            List<TransactionDTO> compteTxs = 
                transactionService.getLastTransactions(compte.getId(), 5);
            transactions.addAll(compteTxs);
        }
        
        // Trier par date décroissante et limiter à 10
        transactions.sort(Comparator.comparing(
            TransactionDTO::getDateTransaction).reversed());
        transactions = transactions.stream()
            .limit(10)
            .collect(Collectors.toList());
        
        // Construire le dashboard
        return DashboardDTO.builder()
            .client(client)
            .comptes(comptes)
            .dernieresTransactions(transactions)
            .statistiques(calculateStats(comptes, transactions))
            .build();
    }
    
    public DashboardDTO getDashboardFallback(Long clientId, Exception e) {
        // Retourner des données en cache ou minimales
        return DashboardDTO.builder()
            .error("Service temporairement indisponible")
            .build();
    }
}
```

## 5. Optimisations

### 5.1 Cache
```yaml
spring:
  cache:
    type: redis
    redis:
      time-to-live: 300000 # 5 minutes
```

### 5.2 Appels parallèles
- Utilisation de CompletableFuture pour les appels indépendants
- Réduction du temps de réponse global

### 5.3 Circuit Breaker
- Protection contre les défaillances en cascade
- Fallback avec données en cache

## 6. Configuration technique

### application.yml
```yaml
server:
  port: 8085

spring:
  application:
    name: composite-service

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/

feign:
  client:
    config:
      default:
        connectTimeout: 5000
        readTimeout: 5000
      client-service:
        url: http://client-service
      compte-service:
        url: http://compte-service
      transaction-service:
        url: http://transaction-service

resilience4j:
  circuitbreaker:
    instances:
      dashboard:
        slidingWindowSize: 10
        failureRateThreshold: 50
        waitDurationInOpenState: 10000
  timelimiter:
    instances:
      dashboard:
        timeoutDuration: 10s
```

## 7. Règles métier

1. Agrégation en temps réel (pas de base de données propre)
2. Timeout global de 10 secondes pour les requêtes
3. Cache des résultats pour 5 minutes
4. Fallback en cas d'indisponibilité d'un service
5. Appels parallèles pour optimiser les performances
6. Limitation à 10 dernières transactions dans le dashboard
7. Pagination pour les recherches avancées
