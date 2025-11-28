# Microservice Notification - Conception détaillée

## 1. Responsabilités
- Envoi de notifications par email
- Envoi de push notifications via Firebase Cloud Messaging (FCM)
- Consommation d'événements RabbitMQ
- Gestion des templates de notifications
- Historique des notifications envoyées

## 2. Architecture logique

```
┌─────────────────────────────────────────────────────────────┐
│                 Notification Service                         │
├─────────────────────────────────────────────────────────────┤
│  Controller Layer                                            │
│  ├── NotificationController (REST API - admin)              │
│                                                              │
│  Service Layer                                               │
│  ├── EmailService (Envoi emails)                            │
│  ├── PushNotificationService (FCM)                          │
│  ├── TemplateService (Templates)                            │
│                                                              │
│  Messaging Layer (Consumers)                                 │
│  ├── ClientEventConsumer                                    │
│  ├── TransactionEventConsumer                               │
│  ├── CompteEventConsumer                                    │
│                                                              │
│  Repository Layer                                            │
│  ├── NotificationRepository (JPA)                           │
└─────────────────────────────────────────────────────────────┘
         │                    │                    │
         ▼                    ▼                    ▼
┌──────────────┐   ┌──────────────┐   ┌──────────────┐
│ PostgreSQL   │   │   RabbitMQ   │   │   Firebase   │
│Notification  │   │   Consumer   │   │     FCM      │
│     DB       │   └──────────────┘   └──────────────┘
└──────────────┘
```

## 3. Modèle de données

### Table: notifications

```sql
CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    type_notification VARCHAR(20) NOT NULL, -- EMAIL, PUSH, SMS
    destinataire VARCHAR(200) NOT NULL, -- email ou device token
    sujet VARCHAR(200),
    contenu TEXT NOT NULL,
    statut VARCHAR(20) NOT NULL, -- EN_ATTENTE, ENVOYEE, ECHEC
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_envoi TIMESTAMP,
    nombre_tentatives INTEGER DEFAULT 0,
    erreur TEXT,
    metadata JSONB, -- event_type, client_id, transaction_id, etc.
    template_id VARCHAR(50)
);

CREATE INDEX idx_notifications_type ON notifications(type_notification);
CREATE INDEX idx_notifications_statut ON notifications(statut);
CREATE INDEX idx_notifications_date ON notifications(date_creation);
CREATE INDEX idx_notifications_destinataire ON notifications(destinataire);
```

### Table: device_tokens

```sql
CREATE TABLE device_tokens (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL,
    device_token VARCHAR(500) NOT NULL UNIQUE,
    platform VARCHAR(20) NOT NULL, -- ANDROID, IOS
    actif BOOLEAN DEFAULT TRUE,
    date_enregistrement TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    derniere_utilisation TIMESTAMP
);

CREATE INDEX idx_device_tokens_client ON device_tokens(client_id);
CREATE INDEX idx_device_tokens_token ON device_tokens(device_token);
```

## 4. APIs REST

### 4.1 Enregistrer un device token (pour push notifications)
```
POST /api/notifications/devices
Content-Type: application/json

Request Body:
{
    "clientId": 1,
    "deviceToken": "fKj8dh3jd...",
    "platform": "ANDROID"
}

Response: 201 Created
```

### 4.2 Envoyer une notification manuelle
```
POST /api/notifications/send
Content-Type: application/json

Request Body:
{
    "type": "EMAIL",
    "destinataire": "client@example.com",
    "sujet": "Test notification",
    "contenu": "Ceci est un test"
}

Response: 202 Accepted
```

### 4.3 Obtenir l'historique des notifications d'un client
```
GET /api/notifications/client/{clientId}?page=0&size=20

Response: 200 OK
```

### 4.4 Obtenir les statistiques
```
GET /api/notifications/stats?dateDebut=2025-11-01&dateFin=2025-11-30

Response: 200 OK
{
    "totalEnvoyees": 1250,
    "totalEchecs": 45,
    "parType": {
        "EMAIL": 800,
        "PUSH": 450
    },
    "tauxSucces": 96.4
}
```

## 5. Événements consommés (RabbitMQ)

### 5.1 CLIENT_CREATED
```
Queue: notification.client.created
Routing Key: client.created

Action:
- Envoyer email de bienvenue
- Template: welcome_email
```

### 5.2 CLIENT_KYC_VALIDATED
```
Queue: notification.client.kyc.validated
Routing Key: client.kyc.validated

Action:
- Envoyer email de confirmation KYC
- Template: kyc_validated_email
```

### 5.3 TRANSACTION_EXECUTED
```
Queue: notification.transaction.executed
Routing Key: transaction.executed

Action:
- Envoyer email de confirmation de transaction
- Envoyer push notification FCM
- Templates: transaction_email, transaction_push
```

### 5.4 COMPTE_CREATED
```
Queue: notification.compte.created
Routing Key: compte.created

Action:
- Envoyer email de confirmation d'ouverture de compte
- Template: compte_created_email
```

### 5.5 COMPTE_STATUS_CHANGED
```
Queue: notification.compte.status.changed
Routing Key: compte.status.changed

Action:
- Envoyer email d'alerte de changement de statut
- Envoyer push notification si compte bloqué
- Template: compte_status_email
```

## 6. Templates de notifications

### 6.1 Email de bienvenue (welcome_email)
```
Sujet: Bienvenue chez WillBank !
Contenu:
Bonjour {{prenom}} {{nom}},

Bienvenue chez WillBank ! Votre compte client {{numeroClient}} a été créé avec succès.

Pour finaliser votre inscription, veuillez compléter votre dossier KYC.

Cordialement,
L'équipe WillBank
```

### 6.2 Email de transaction (transaction_email)
```
Sujet: Transaction effectuée - {{reference}}
Contenu:
Bonjour {{prenom}},

Une transaction a été effectuée sur votre compte {{numeroCompte}}.

Type: {{typeTransaction}}
Montant: {{montant}} {{devise}}
Date: {{dateTransaction}}
Nouveau solde: {{nouveauSolde}} {{devise}}

Si vous n'êtes pas à l'origine de cette transaction, contactez-nous immédiatement.

Cordialement,
L'équipe WillBank
```

### 6.3 Push notification de transaction (transaction_push)
```json
{
    "title": "Transaction effectuée",
    "body": "{{typeTransaction}} de {{montant}} XOF sur votre compte",
    "data": {
        "type": "TRANSACTION",
        "transactionId": "{{transactionId}}",
        "reference": "{{reference}}"
    }
}
```

## 7. Configuration Firebase Cloud Messaging

### Initialisation FCM
```java
@Configuration
public class FirebaseConfig {
    @PostConstruct
    public void initialize() {
        try {
            FileInputStream serviceAccount = 
                new FileInputStream("firebase-service-account.json");
            
            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();
            
            FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            log.error("Erreur initialisation Firebase", e);
        }
    }
}
```

### Envoi de push notification
```java
public void sendPushNotification(String deviceToken, String title, 
                                  String body, Map<String, String> data) {
    Message message = Message.builder()
        .setToken(deviceToken)
        .setNotification(Notification.builder()
            .setTitle(title)
            .setBody(body)
            .build())
        .putAllData(data)
        .build();
    
    try {
        String response = FirebaseMessaging.getInstance().send(message);
        log.info("Push notification envoyée: {}", response);
    } catch (FirebaseMessagingException e) {
        log.error("Erreur envoi push notification", e);
    }
}
```

## 8. Configuration Email (SMTP)

### application.yml
```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: ${SMTP_USERNAME}
    password: ${SMTP_PASSWORD}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
```

## 9. Configuration RabbitMQ

### Queues et Bindings
```java
@Configuration
public class RabbitMQConfig {
    
    @Bean
    public Queue clientCreatedQueue() {
        return new Queue("notification.client.created", true);
    }
    
    @Bean
    public Queue transactionExecutedQueue() {
        return new Queue("notification.transaction.executed", true);
    }
    
    @Bean
    public Binding clientCreatedBinding() {
        return BindingBuilder
            .bind(clientCreatedQueue())
            .to(clientExchange())
            .with("client.created");
    }
    
    @Bean
    public TopicExchange clientExchange() {
        return new TopicExchange("client.events");
    }
}
```

## 10. Règles métier

1. Toutes les notifications sont enregistrées en base
2. Retry automatique en cas d'échec (max 3 tentatives)
3. Les push notifications nécessitent un device token valide
4. Les emails utilisent des templates personnalisables
5. Les notifications critiques (compte bloqué) sont prioritaires
6. Désactivation automatique des device tokens invalides
7. Rate limiting pour éviter le spam

## 11. Configuration technique

### application.yml
```yaml
server:
  port: 8084

spring:
  application:
    name: notification-service
  datasource:
    url: jdbc:postgresql://localhost:5432/willbank_notifications
    username: willbank
    password: ${DB_PASSWORD}
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/

notification:
  retry:
    maxAttempts: 3
    backoffDelay: 5000
  firebase:
    configPath: firebase-service-account.json
```
