# Guide de test - WillBank Microservices

## Démarrage rapide

### 1. Démarrer l'infrastructure
```bash
cd infrastructure
docker-compose up -d
```

### 2. Démarrer les services dans l'ordre

#### Terminal 1 - Discovery Service
```bash
cd services/discovery-service
mvn clean install
mvn spring-boot:run
```
Attendre que le service démarre sur http://localhost:8761

#### Terminal 2 - Client Service
```bash
cd services/client-service
mvn clean install
mvn spring-boot:run
```
Le service démarre sur http://localhost:8081

## Tests avec Postman ou cURL

### Client Service - Tests

#### 1. Créer un client
```bash
curl -X POST http://localhost:8081/api/clients \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "DIALLO",
    "prenom": "Mamadou",
    "dateNaissance": "1990-05-15",
    "adresse": "Dakar, Senegal",
    "telephone": "+221771234567",
    "email": "mamadou.diallo@example.com",
    "typeClient": "PARTICULIER"
  }'
```

Réponse attendue (201 Created):
```json
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
  "kycDateValidation": null,
  "dateCreation": "2025-11-26T10:30:00",
  "dateModification": "2025-11-26T10:30:00",
  "version": 0
}
```

#### 2. Obtenir un client par ID
```bash
curl http://localhost:8081/api/clients/1
```

#### 3. Obtenir un client par numéro
```bash
curl http://localhost:8081/api/clients/numero/CLI20250001
```

#### 4. Lister tous les clients (avec pagination)
```bash
curl "http://localhost:8081/api/clients?page=0&size=10&sort=dateCreation,desc"
```

#### 5. Rechercher des clients
```bash
curl "http://localhost:8081/api/clients/search?term=DIALLO&page=0&size=10"
```

#### 6. Mettre à jour un client
```bash
curl -X PUT "http://localhost:8081/api/clients/1?version=0" \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "DIALLO",
    "prenom": "Mamadou",
    "dateNaissance": "1990-05-15",
    "adresse": "Nouvelle adresse à Dakar",
    "telephone": "+221771234567",
    "email": "mamadou.diallo@example.com",
    "typeClient": "PARTICULIER"
  }'
```

#### 7. Changer le statut d'un client
```bash
curl -X PATCH http://localhost:8081/api/clients/1/statut \
  -H "Content-Type: application/json" \
  -d '{
    "statut": "ACTIF"
  }'
```

#### 8. Valider le KYC d'un client
```bash
curl -X POST http://localhost:8081/api/clients/1/kyc/valider
```

#### 9. Supprimer un client (soft delete)
```bash
curl -X DELETE http://localhost:8081/api/clients/1
```

### Vérification des événements RabbitMQ

1. Accéder à l'interface RabbitMQ Management: http://localhost:15672
2. Login: guest / guest
3. Aller dans l'onglet "Queues"
4. Vérifier que les queues suivantes existent:
   - notification.client.created
   - notification.client.updated
   - notification.client.kyc.validated
   - notification.client.status.changed
5. Cliquer sur une queue pour voir les messages

### Vérification dans PostgreSQL

```bash
# Se connecter à la base de données
docker exec -it willbank-postgres-client psql -U willbank -d willbank_clients

# Lister les clients
SELECT * FROM clients;

# Vérifier un client spécifique
SELECT * FROM clients WHERE numero_client = 'CLI20250001';

# Quitter
\q
```

## Collection Postman

### Créer une collection Postman avec les requêtes suivantes:

**Variables d'environnement:**
- `base_url`: http://localhost:8081
- `client_id`: 1
- `version`: 0

**Requêtes:**

1. **Create Client**
   - Method: POST
   - URL: {{base_url}}/api/clients
   - Body: (voir exemple ci-dessus)

2. **Get Client by ID**
   - Method: GET
   - URL: {{base_url}}/api/clients/{{client_id}}

3. **Get Client by Numero**
   - Method: GET
   - URL: {{base_url}}/api/clients/numero/CLI20250001

4. **List All Clients**
   - Method: GET
   - URL: {{base_url}}/api/clients?page=0&size=10

5. **Search Clients**
   - Method: GET
   - URL: {{base_url}}/api/clients/search?term=DIALLO

6. **Update Client**
   - Method: PUT
   - URL: {{base_url}}/api/clients/{{client_id}}?version={{version}}
   - Body: (voir exemple ci-dessus)

7. **Change Client Status**
   - Method: PATCH
   - URL: {{base_url}}/api/clients/{{client_id}}/statut
   - Body: {"statut": "ACTIF"}

8. **Validate KYC**
   - Method: POST
   - URL: {{base_url}}/api/clients/{{client_id}}/kyc/valider

9. **Delete Client**
   - Method: DELETE
   - URL: {{base_url}}/api/clients/{{client_id}}

## Scénarios de test

### Scénario 1: Création et activation d'un client
1. Créer un client → Statut EN_ATTENTE
2. Valider le KYC → Statut ACTIF, kycValide = true
3. Vérifier l'événement dans RabbitMQ

### Scénario 2: Gestion des erreurs
1. Créer un client avec un email
2. Tenter de créer un autre client avec le même email → Erreur 409 Conflict
3. Tenter d'obtenir un client inexistant → Erreur 404 Not Found

### Scénario 3: Mise à jour avec optimistic locking
1. Créer un client (version = 0)
2. Mettre à jour avec version = 0 → Succès (version = 1)
3. Tenter de mettre à jour avec version = 0 → Erreur 409 Conflict

### Scénario 4: Recherche et pagination
1. Créer plusieurs clients
2. Lister avec pagination (page 0, size 5)
3. Rechercher par nom
4. Filtrer par statut

## Logs à surveiller

### Client Service
```bash
# Voir les logs en temps réel
cd services/client-service
mvn spring-boot:run

# Logs attendus:
# - "Creating new client: DIALLO Mamadou"
# - "Client created with ID: 1 and numero: CLI20250001"
# - "Published CLIENT_CREATED event for client: CLI20250001"
```

### RabbitMQ
- Vérifier que les messages sont bien publiés dans les queues
- Vérifier le format JSON des événements

## Troubleshooting

### Problème: Service ne démarre pas
- Vérifier que PostgreSQL est démarré: `docker ps | grep postgres`
- Vérifier que RabbitMQ est démarré: `docker ps | grep rabbitmq`
- Vérifier que Eureka est démarré: http://localhost:8761

### Problème: Erreur de connexion à la base de données
```bash
# Vérifier la connexion
docker exec -it willbank-postgres-client psql -U willbank -d willbank_clients -c "SELECT 1"
```

### Problème: Les événements ne sont pas publiés
- Vérifier les logs du service
- Vérifier la connexion RabbitMQ: http://localhost:15672
- Vérifier la configuration dans application.yml

## Prochaines étapes

Une fois le Client Service testé et fonctionnel:
1. Implémenter le Compte Service (même structure)
2. Tester la création de comptes pour un client
3. Implémenter le Transaction Service
4. Tester les virements entre comptes
5. Implémenter le Notification Service
6. Vérifier la réception des notifications
7. Implémenter le Composite Service
8. Tester l'agrégation de données
9. Implémenter l'API Gateway
10. Tester le routage et l'authentification
