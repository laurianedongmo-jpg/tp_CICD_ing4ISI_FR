# Guide de démarrage rapide - WillBank Microservices

## Prérequis à installer

### 1. Java JDK 17 ou supérieur
**Téléchargement :** https://adoptium.net/

Vérifier l'installation :
```bash
java -version
```

### 2. Maven 3.8+
**Téléchargement :** https://maven.apache.org/download.cgi

**Installation Windows :**
1. Télécharger le fichier ZIP
2. Extraire dans `C:\Program Files\Apache\maven`
3. Ajouter au PATH : `C:\Program Files\Apache\maven\bin`

Vérifier l'installation :
```bash
mvn -version
```

### 3. Docker Desktop (Optionnel mais recommandé)
**Téléchargement :** https://www.docker.com/products/docker-desktop

Pour PostgreSQL, RabbitMQ, Redis

## Option 1 : Avec Docker (Recommandé)

### Étape 1 : Démarrer l'infrastructure
```bash
cd infrastructure
docker compose up -d
```

### Étape 2 : Vérifier les services
```bash
docker ps
```

Vous devriez voir :
- PostgreSQL (4 instances sur ports 5432-5435)
- RabbitMQ (ports 5672, 15672)
- Redis (port 6379)

### Étape 3 : Démarrer Discovery Service
```bash
cd services/discovery-service
mvn clean install
mvn spring-boot:run
```

Accéder à : http://localhost:8761

### Étape 4 : Démarrer Client Service
```bash
cd services/client-service
mvn clean install
mvn spring-boot:run
```

Le service démarre sur : http://localhost:8081

## Option 2 : Sans Docker (Mode développement simplifié)

Cette option utilise H2 (base de données en mémoire) au lieu de PostgreSQL.

### Étape 1 : Démarrer Discovery Service
```bash
cd services/discovery-service
mvn clean install
mvn spring-boot:run
```

### Étape 2 : Démarrer Client Service
```bash
cd services/client-service
mvn clean install
mvn spring-boot:run
```

**Note :** RabbitMQ est optionnel en mode dev. Les événements ne seront pas publiés mais le service fonctionnera.

## Option 3 : Avec votre IDE (IntelliJ IDEA / Eclipse / VS Code)

### IntelliJ IDEA
1. Ouvrir le projet : `File > Open` → Sélectionner le dossier racine
2. Attendre l'import Maven
3. Clic droit sur `DiscoveryServiceApplication.java` → `Run`
4. Clic droit sur `ClientServiceApplication.java` → `Run`

### VS Code
1. Installer l'extension "Extension Pack for Java"
2. Ouvrir le dossier du projet
3. Utiliser le Spring Boot Dashboard pour démarrer les services

### Eclipse
1. `File > Import > Maven > Existing Maven Projects`
2. Sélectionner les dossiers des services
3. Clic droit sur le projet → `Run As > Spring Boot App`

## Tester le Client Service

### Test 1 : Créer un client
```bash
curl -X POST http://localhost:8081/api/clients ^
  -H "Content-Type: application/json" ^
  -d "{\"nom\":\"DIALLO\",\"prenom\":\"Mamadou\",\"dateNaissance\":\"1990-05-15\",\"adresse\":\"Dakar, Senegal\",\"telephone\":\"+221771234567\",\"email\":\"mamadou.diallo@example.com\",\"typeClient\":\"PARTICULIER\"}"
```

### Test 2 : Lister les clients
```bash
curl http://localhost:8081/api/clients
```

### Test 3 : Obtenir un client par ID
```bash
curl http://localhost:8081/api/clients/1
```

### Test 4 : Valider le KYC
```bash
curl -X POST http://localhost:8081/api/clients/1/kyc/valider
```

## Accès aux interfaces web

- **Eureka Dashboard :** http://localhost:8761
- **H2 Console (Client Service) :** http://localhost:8081/h2-console
  - JDBC URL: `jdbc:h2:mem:willbank_clients`
  - Username: `sa`
  - Password: (laisser vide)
- **RabbitMQ Management (si Docker) :** http://localhost:15672
  - Username: `guest`
  - Password: `guest`

## Troubleshooting

### Erreur : "mvn command not found"
Maven n'est pas installé ou pas dans le PATH. Installer Maven et l'ajouter au PATH.

### Erreur : "java command not found"
Java n'est pas installé. Installer JDK 17+.

### Erreur : Port déjà utilisé
Un autre service utilise le port. Changer le port dans `application.yml` :
```yaml
server:
  port: 8082  # Changer le port
```

### Erreur : Cannot connect to database
Si vous utilisez PostgreSQL, vérifier que Docker est démarré :
```bash
docker ps | grep postgres
```

Si vous utilisez H2, vérifier la configuration dans `application.yml`.

### Les services ne s'enregistrent pas dans Eureka
1. Vérifier que Eureka est démarré sur le port 8761
2. Vérifier les logs du service
3. Attendre 30 secondes (délai d'enregistrement)

## Prochaines étapes

Une fois le Client Service fonctionnel :

1. **Implémenter le Compte Service** (même structure que Client Service)
2. **Implémenter le Transaction Service** (avec Feign Client vers Compte Service)
3. **Implémenter le Notification Service** (avec RabbitMQ Consumers)
4. **Implémenter le Composite Service** (agrégation de données)
5. **Implémenter l'API Gateway** (routage et sécurité)

## Ressources

- **Documentation Spring Boot :** https://spring.io/projects/spring-boot
- **Documentation Spring Cloud :** https://spring.io/projects/spring-cloud
- **Documentation Eureka :** https://cloud.spring.io/spring-cloud-netflix/
- **Documentation RabbitMQ :** https://www.rabbitmq.com/documentation.html

## Support

Consultez les fichiers de documentation dans le dossier `docs/` pour plus de détails sur l'architecture et l'implémentation.
