# WillBank - Architecture Microservices

## Contexte
Migration d'une application bancaire monolithique vers une architecture microservices pour améliorer la scalabilité, la résilience et l'intégration de nouveaux canaux.

## Structure du Projet

```
willbank-microservices/
├── docs/
│   ├── architecture-globale.md
│   ├── microservices/
│   │   ├── client-service.md
│   │   ├── compte-service.md
│   │   ├── transaction-service.md
│   │   ├── notification-service.md
│   │   ├── composite-service.md
│   │   └── gateway-service.md
│   └── diagrammes/
├── services/
│   ├── client-service/
│   ├── compte-service/
│   ├── transaction-service/
│   ├── notification-service/
│   ├── composite-service/
│   ├── api-gateway/
│   └── discovery-service/
└── infrastructure/
    ├── rabbitmq/
    └── docker-compose.yml
```

## Technologies
- **Backend**: Spring Boot (Java)
- **Service Discovery**: Eureka
- **API Gateway**: Spring Cloud Gateway
- **Message Broker**: RabbitMQ
- **Notifications**: Firebase Cloud Messaging (FCM)
- **Bases de données**: PostgreSQL (par microservice)
- **Conteneurisation**: Docker
