# Installation - WillBank Microservices

## Étape 1 : Installer les prérequis

### 1.1 Java JDK 17
1. Télécharger depuis : https://adoptium.net/
2. Choisir : **Temurin 17 (LTS)**
3. Installer avec les options par défaut
4. Vérifier : Ouvrir un nouveau terminal et taper `java -version`

### 1.2 Maven
1. Télécharger depuis : https://maven.apache.org/download.cgi
2. Choisir : **Binary zip archive** (apache-maven-3.9.x-bin.zip)
3. Extraire dans : `C:\Program Files\Apache\maven`
4. Ajouter au PATH :
   - Ouvrir : Panneau de configuration → Système → Paramètres système avancés
   - Cliquer sur "Variables d'environnement"
   - Dans "Variables système", trouver "Path" et cliquer "Modifier"
   - Ajouter : `C:\Program Files\Apache\maven\bin`
   - Cliquer OK
5. **IMPORTANT :** Fermer et rouvrir le terminal
6. Vérifier : `mvn -version`

### 1.3 cURL (pour les tests)
- Windows 10/11 : cURL est déjà installé
- Vérifier : `curl --version`

## Étape 2 : Démarrer les services

### Option A : Avec le script automatique (Recommandé)

Double-cliquer sur : **start-services.bat**

Le script va :
1. Vérifier Java et Maven
2. Compiler les services
3. Démarrer Discovery Service (Eureka)
4. Démarrer Client Service

### Option B : Manuellement

#### Terminal 1 - Discovery Service
```bash
cd services\discovery-service
mvn clean install -DskipTests
mvn spring-boot:run
```

Attendre le message : "Started DiscoveryServiceApplication"

#### Terminal 2 - Client Service
```bash
cd services\client-service
mvn clean install -DskipTests
mvn spring-boot:run
```

Attendre le message : "Started ClientServiceApplication"

## Étape 3 : Vérifier que tout fonctionne

### 3.1 Vérifier Eureka
Ouvrir dans le navigateur : http://localhost:8761

Vous devriez voir :
- **Instances currently registered with Eureka**
- **CLIENT-SERVICE** avec 1 instance

### 3.2 Vérifier H2 Console
Ouvrir dans le navigateur : http://localhost:8081/h2-console

Paramètres de connexion :
- JDBC URL : `jdbc:h2:mem:willbank_clients`
- User Name : `sa`
- Password : (laisser vide)

Cliquer "Connect" puis exécuter :
```sql
SELECT * FROM CLIENTS;
```

### 3.3 Tester l'API

#### Avec le script de test
Double-cliquer sur : **test-client-service.bat**

#### Manuellement avec cURL

**Créer un client :**
```bash
curl -X POST http://localhost:8081/api/clients -H "Content-Type: application/json" -d "{\"nom\":\"DIALLO\",\"prenom\":\"Mamadou\",\"dateNaissance\":\"1990-05-15\",\"adresse\":\"Dakar, Senegal\",\"telephone\":\"+221771234567\",\"email\":\"mamadou.diallo@example.com\",\"typeClient\":\"PARTICULIER\"}"
```

**Lister les clients :**
```bash
curl http://localhost:8081/api/clients
```

**Obtenir un client :**
```bash
curl http://localhost:8081/api/clients/1
```

**Valider le KYC :**
```bash
curl -X POST http://localhost:8081/api/clients/1/kyc/valider
```

## Étape 4 : Tester avec Postman (Optionnel)

1. Télécharger Postman : https://www.postman.com/downloads/
2. Créer une nouvelle collection "WillBank"
3. Ajouter les requêtes du fichier TESTING-GUIDE.md

## Problèmes courants

### "mvn n'est pas reconnu"
- Maven n'est pas dans le PATH
- Solution : Ajouter Maven au PATH et **redémarrer le terminal**

### "java n'est pas reconnu"
- Java n'est pas installé ou pas dans le PATH
- Solution : Installer Java 17+ et redémarrer le terminal

### Port 8081 déjà utilisé
- Un autre service utilise le port
- Solution : Arrêter l'autre service ou changer le port dans `application.yml`

### Le service ne démarre pas
- Vérifier les logs dans le terminal
- Vérifier que Java 17+ est installé : `java -version`
- Vérifier que Maven fonctionne : `mvn -version`

### CLIENT-SERVICE n'apparaît pas dans Eureka
- Attendre 30 secondes (délai d'enregistrement)
- Vérifier que Eureka est démarré sur le port 8761
- Vérifier les logs du Client Service

## Prochaines étapes

Une fois que le Client Service fonctionne :

1. ✅ Discovery Service - **FONCTIONNEL**
2. ✅ Client Service - **FONCTIONNEL**
3. ⏳ Compte Service - À implémenter
4. ⏳ Transaction Service - À implémenter
5. ⏳ Notification Service - À implémenter
6. ⏳ Composite Service - À implémenter
7. ⏳ API Gateway - À implémenter

Consultez le fichier **docs/guide-implementation.md** pour continuer l'implémentation.

## Aide

Pour toute question, consultez :
- **QUICK-START.md** - Guide de démarrage rapide
- **TESTING-GUIDE.md** - Guide de test complet
- **docs/** - Documentation complète de l'architecture
