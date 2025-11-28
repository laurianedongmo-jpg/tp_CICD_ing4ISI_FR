# Architecture Globale - WillBank Microservices

## 1. Vue d'ensemble de l'architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                    Applications Clientes                         │
│              (Web CRM, Mobile App, Partenaires)                  │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│                      API GATEWAY                                 │
│         (Authentification, Routage, Rate Limiting)               │
└────────┬────────────────────────────────────────────────────────┘
         │
         ├──────────────┬──────────────┬──────────────┬───────────┐
         ▼              ▼              ▼              ▼           ▼
┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐
│   Client    │ │   Compte    │ │ Transaction │ │ Composite   │ │Notification │
│   Service   │ │   Service   │ │   Service   │ │   Service   │ │  Service    │
└──────┬──────┘ └──────┬──────┘ └──────┬──────┘ └──────┬──────┘ └──────┬──────┘
       │               │               │               │               │
       ▼               ▼               ▼               │               │
┌─────────────┐ ┌─────────────┐ ┌─────────────┐      │               │
│  Client DB  │ │  Compte DB  │ │Transaction  │      │               │
│ (PostgreSQL)│ │ (PostgreSQL)│ │DB(PostgreSQL)│     │               │
└─────────────┘ └─────────────┘ └─────────────┘      │               │
                                       │               │               │
                                       └───────┬───────┘               │
                                               ▼                       │
                                        ┌─────────────┐                │
                                        │  RabbitMQ   │◄───────────────┘
                                        │   Broker    │
                                        └─────────────┘
                                               │
                                               ▼
                                        ┌─────────────┐
                                        │  Firebase   │
                                        │     FCM     │
                                        └─────────────┘
                                               │
                                               ▼
                                        ┌─────────────┐
                                        │   Mobile    │
                                        │    Push     │
                                        └─────────────┘

┌─────────────────────────────────────────────────────────────────┐
│              Eureka Discovery Service                            │
│         (Enregistrement et découverte des services)              │
└─────────────────────────────────────────────────────────────────┘
```

## 2. Principes architecturaux

### 2.1 Isolation des données
- Chaque microservice possède sa propre base de données
- Pas d'accès direct aux bases de données d'autres services
- Communication uniquement via APIs REST ou événements

### 2.2 Communication
- **Synchrone**: REST/HTTP pour les requêtes-réponses
- **Asynchrone**: RabbitMQ pour les événements (notifications, audit)

### 2.3 Patterns utilisés
- **API Gateway Pattern**: Point d'entrée unique
- **Service Discovery**: Eureka pour l'enregistrement dynamique
- **Database per Service**: Isolation des données
- **Event-Driven**: RabbitMQ pour découplage
- **API Composition**: Service composite pour agrégation

## 3. Flux principaux

### 3.1 Création d'un client
1. Client → API Gateway → Client Service
2. Client Service → Enregistre dans Client DB
3. Client Service → Publie événement "ClientCreated" sur RabbitMQ
4. Notification Service → Consomme événement → Envoie email de bienvenue

### 3.2 Consultation du dashboard
1. Client → API Gateway → Composite Service
2. Composite Service → Client Service (infos client)
3. Composite Service → Compte Service (liste comptes + soldes)
4. Composite Service → Transaction Service (dernières transactions)
5. Composite Service → Agrège et retourne les données

### 3.3 Exécution d'une transaction
1. Client → API Gateway → Transaction Service
2. Transaction Service → Vérifie le compte (Compte Service)
3. Transaction Service → Enregistre la transaction
4. Transaction Service → Met à jour le solde (Compte Service)
5. Transaction Service → Publie événement "TransactionExecuted"
6. Notification Service → Envoie email + push notification FCM

## 4. Sécurité et résilience

### 4.1 Sécurité
- Authentification JWT au niveau de l'API Gateway
- HTTPS pour toutes les communications externes
- Validation des données à chaque niveau

### 4.2 Résilience
- Circuit Breaker (Resilience4j) pour les appels inter-services
- Retry avec backoff exponentiel
- Timeout configurables
- Health checks pour chaque service

## 5. Scalabilité

- Chaque microservice peut être déployé en plusieurs instances
- Load balancing automatique via Eureka
- Scalabilité horizontale selon la charge
- RabbitMQ pour absorber les pics de charge

## 6. Monitoring et observabilité

- Logs centralisés (ELK Stack recommandé)
- Métriques (Prometheus + Grafana)
- Tracing distribué (Zipkin/Jaeger)
- Health endpoints pour chaque service
