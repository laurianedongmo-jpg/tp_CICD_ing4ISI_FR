# Guide d'implémentation - WillBank Microservices

## Phase 1 : Préparation de l'environnement

### 1.1 Prérequis
- JDK 17 ou supérieur
- Maven 3.8+
- Docker et Docker Compose
- IDE (IntelliJ IDEA, Eclipse, VS Code)
- Compte Firebase (pour FCM)

### 1.2 Démarrage de l'infrastructure
```bash
cd infrastructure
docker-compose up -d
```

Vérifier que tous les services sont démarrés :
- PostgreSQL Client: localhost:5432
- PostgreSQL Compte: localhost:5433
- PostgreSQL Transaction: localhost:5434
- PostgreSQL Notification: localhost:5435
- RabbitMQ: localhost:5672 (Management UI: localhost:15672)
- Redis: localhost:6379

## Phase 2 : Ordre d'implémentation des services

### 2.1 Discovery Service (Eureka) - PRIORITÉ 1
C'est le premier service à implémenter car tous les autres s'y enregistrent.

**Étapes :**
1. Créer un projet Spring Boot avec Spring Cloud Netflix Eureka Server
2. Ajouter @EnableEurekaServer
3. Configurer application.yml
4. Démarrer sur le port 8761
5. Vérifier l'interface web: http://localhost:8761

### 2.2 Client Service - PRIORITÉ 2
Service de base sans dépendances vers d'autres microservices.

**Étapes :**
1. Créer le projet Spring Boot
2. Implémenter les entités (Client, DocumentKYC)
3. Créer les repositories JPA
4. Implémenter les services métier
5. Créer les controllers REST
6. Configurer RabbitMQ pour publier les événements
7. Tester les APIs avec Postman

### 2.3 Compte Service - PRIORITÉ 3
Dépend du Client Service pour vérifier l'existence du client.

**Étapes :**
1. Créer le projet Spring Boot
2. Implémenter l'entité Compte
3. Créer le repository
4. Implémenter les services métier
5. Ajouter Feign Client pour appeler Client Service (optionnel)
6. Créer les controllers REST
7. Configurer RabbitMQ
8. Tester les APIs

### 2.4 Transaction Service - PRIORITÉ 4
Dépend du Compte Service pour les opérations sur les soldes.

**Étapes :**
1. Créer le projet Spring Boot
2. Implémenter l'entité Transaction
3. Créer le repository
4. Configurer Feign Client pour Compte Service
5. Implémenter les services métier (virements, dépôts, retraits)
6. Créer les controllers REST
7. Configurer RabbitMQ
8. Implémenter Circuit Breaker (Resilience4j)
9. Tester les scénarios de transaction

### 2.5 Notification Service - PRIORITÉ 5
Consomme les événements des autres services.

**Étapes :**
1. Créer le projet Spring Boot
2. Implémenter les entités (Notification, DeviceToken)
3. Configurer Firebase Cloud Messaging
4. Implémenter EmailService (SMTP)
5. Implémenter PushNotificationService (FCM)
6. Créer les consumers RabbitMQ
7. Implémenter les templates de notifications
8. Tester l'envoi d'emails et de push notifications

### 2.6 Composite Service - PRIORITÉ 6
Agrège les données des autres services.

**Étapes :**
1. Créer le projet Spring Boot
2. Configurer Feign Clients pour tous les services
3. Implémenter DashboardService
4. Implémenter ReleveService
5. Implémenter SearchService
6. Créer les controllers REST
7. Configurer Circuit Breaker
8. Implémenter le cache (Redis)
9. Tester les agrégations

### 2.7 API Gateway - PRIORITÉ 7
Point d'entrée unique, à implémenter en dernier.

**Étapes :**
1. Créer le projet Spring Cloud Gateway
2. Configurer les routes vers tous les services
3. Implémenter l'authentification JWT
4. Configurer le rate limiting
5. Implémenter CORS
6. Ajouter les filtres de logging
7. Configurer Circuit Breaker
8. Tester le routage

## Phase 3 : Structure d'un microservice type

```
service-name/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/willbank/servicename/
│   │   │       ├── ServiceNameApplication.java
│   │   │       ├── config/
│   │   │       │   ├── RabbitMQConfig.java
│   │   │       │   └── SecurityConfig.java
│   │   │       ├── controller/
│   │   │       │   └── XxxController.java
│   │   │       ├── service/
│   │   │       │   ├── XxxService.java
│   │   │       │   └── impl/
│   │   │       │       └── XxxServiceImpl.java
│   │   │       ├── repository/
│   │   │       │   └── XxxRepository.java
│   │   │       ├── entity/
│   │   │       │   └── Xxx.java
│   │   │       ├── dto/
│   │   │       │   ├── XxxDTO.java
│   │   │       │   └── XxxRequest.java
│   │   │       ├── messaging/
│   │   │       │   ├── publisher/
│   │   │       │   │   └── XxxEventPublisher.java
│   │   │       │   └── consumer/
│   │   │       │       └── XxxEventConsumer.java
│   │   │       ├── client/
│   │   │       │   └── XxxServiceClient.java (Feign)
│   │   │       └── exception/
│   │   │           ├── GlobalExceptionHandler.java
│   │   │           └── CustomExceptions.java
│   │   └── resources/
│   │       ├── application.yml
│   │       └── application-dev.yml
│   └── test/
│       └── java/
│           └── com/willbank/servicename/
│               ├── controller/
│               ├── service/
│               └── integration/
└── pom.xml
```

## Phase 4 : Dépendances Maven communes

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.0</version>
</parent>

<properties>
    <java.version>17</java.version>
    <spring-cloud.version>2023.0.0</spring-cloud.version>
</properties>

<dependencies>
    <!-- Spring Boot -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    
    <!-- PostgreSQL -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
    </dependency>
    
    <!-- Eureka Client -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    
    <!-- RabbitMQ -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-amqp</artifactId>
    </dependency>
    
    <!-- Feign Client -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-openfeign</artifactId>
    </dependency>
    
    <!-- Resilience4j -->
    <dependency>
        <groupId>io.github.resilience4j</groupId>
        <artifactId>resilience4j-spring-boot3</artifactId>
    </dependency>
    
    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    
    <!-- Testing -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>${spring-cloud.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

## Phase 5 : Tests

### 5.1 Tests unitaires
- Tester les services métier
- Mocker les repositories et clients Feign

### 5.2 Tests d'intégration
- Tester les controllers avec MockMvc
- Tester les repositories avec @DataJpaTest

### 5.3 Tests end-to-end
- Démarrer tous les services
- Tester les flux complets (création client → compte → transaction)

## Phase 6 : Déploiement

### 6.1 Dockerisation de chaque service
```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 6.2 Docker Compose complet
Ajouter tous les microservices au docker-compose.yml

## Conseils d'implémentation

1. **Commencer simple** : Implémenter d'abord les fonctionnalités de base
2. **Tester régulièrement** : Tester chaque API après implémentation
3. **Utiliser Postman** : Créer une collection pour tous les endpoints
4. **Logs** : Ajouter des logs détaillés pour le debugging
5. **Gestion d'erreurs** : Implémenter un GlobalExceptionHandler
6. **Documentation** : Utiliser Swagger/OpenAPI pour documenter les APIs
7. **Versioning** : Utiliser le versioning optimiste pour éviter les conflits
8. **Transactions** : Utiliser @Transactional pour les opérations critiques
