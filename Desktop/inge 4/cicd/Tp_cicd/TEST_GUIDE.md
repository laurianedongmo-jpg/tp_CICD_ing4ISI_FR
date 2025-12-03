# 🧪 Guide de Test - Application CI/CD

## 1️⃣ Tester le Pipeline GitHub Actions

### Étape 1 : Voir le Pipeline en Action

1. Allez sur : https://github.com/laurianedongmo-jpg/tp_CICD_ing4ISI_FR
2. Cliquez sur l'onglet **"Actions"**
3. Vous devriez voir un workflow en cours ou terminé

### Étape 2 : Configurer le Secret pour les Tests

Le pipeline a besoin d'un secret pour fonctionner :

1. Sur GitHub, allez dans **Settings** → **Secrets and variables** → **Actions**
2. Cliquez sur **"New repository secret"**
3. Ajoutez :
   - **Name** : `DB_PASSWORD`
   - **Secret** : `TestPassword123!`
4. Cliquez sur **"Add secret"**

### Étape 3 : Déclencher le Pipeline

Pour tester le pipeline, faites un petit changement :

```bash
# Modifier un fichier
echo "# Test Pipeline" >> README.md

# Commiter et pousser
git add README.md
git commit -m "test: Trigger CI/CD pipeline"
git push origin main
```

Retournez dans l'onglet **Actions** pour voir le pipeline s'exécuter !

---

## 2️⃣ Tester l'Application en Local

### Option A : Avec Docker Compose (Recommandé)

#### Étape 1 : Créer le fichier .env

```bash
# Copier le template
cp .env.example .env
```

Éditez `.env` avec ces valeurs :
```env
DB_NAME=tp_cicd_db
DB_USERNAME=tp_user
DB_PASSWORD=MyPassword123!
DB_PORT=3306
APP_PORT=8080
DOCKER_USERNAME=votre_username
```

#### Étape 2 : Démarrer l'application

```bash
# Démarrer tous les services
docker-compose up -d

# Voir les logs
docker-compose logs -f
```

#### Étape 3 : Tester l'application

**Dans votre navigateur :**
- Health Check : http://localhost:8080/actuator/health
- Métriques : http://localhost:8080/actuator/metrics
- Info : http://localhost:8080/actuator/info

**Avec curl :**
```bash
curl http://localhost:8080/actuator/health
```

**Résultat attendu :**
```json
{
  "status": "UP"
}
```

#### Étape 4 : Vérifier la base de données

```bash
# Se connecter à MySQL
docker-compose exec mysql mysql -u tp_user -p

# Entrer le mot de passe : MyPassword123!

# Lister les bases de données
SHOW DATABASES;

# Utiliser la base
USE tp_cicd_db;

# Lister les tables
SHOW TABLES;
```

#### Étape 5 : Arrêter l'application

```bash
# Arrêter les services
docker-compose down

# Arrêter et supprimer les données
docker-compose down -v
```

---

### Option B : Avec Maven (Sans Docker)

#### Prérequis
- MySQL installé et démarré
- Java 21 installé

#### Étape 1 : Créer la base de données

```sql
CREATE DATABASE tp_cicd_db;
CREATE USER 'tp_user'@'localhost' IDENTIFIED BY 'MyPassword123!';
GRANT ALL PRIVILEGES ON tp_cicd_db.* TO 'tp_user'@'localhost';
FLUSH PRIVILEGES;
```

#### Étape 2 : Configurer les variables d'environnement

**Windows (PowerShell) :**
```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/tp_cicd_db"
$env:DB_USERNAME="tp_user"
$env:DB_PASSWORD="MyPassword123!"
```

**Linux/Mac :**
```bash
export DB_URL="jdbc:mysql://localhost:3306/tp_cicd_db"
export DB_USERNAME="tp_user"
export DB_PASSWORD="MyPassword123!"
```

#### Étape 3 : Compiler et exécuter

```bash
# Compiler
mvn clean package -DskipTests

# Exécuter
java -jar target/Tp_cicd-0.0.1-SNAPSHOT.jar
```

#### Étape 4 : Tester

```bash
curl http://localhost:8080/actuator/health
```

---

## 3️⃣ Tester le Build Docker

### Étape 1 : Build l'application

```bash
mvn clean package -DskipTests
```

### Étape 2 : Build l'image Docker

```bash
docker build -t tp-cicd-app:test .
```

### Étape 3 : Exécuter le conteneur

```bash
docker run -d \
  -p 8080:8080 \
  -e DB_URL=jdbc:mysql://host.docker.internal:3306/tp_cicd_db \
  -e DB_USERNAME=tp_user \
  -e DB_PASSWORD=MyPassword123! \
  --name tp-cicd-test \
  tp-cicd-app:test
```

### Étape 4 : Vérifier les logs

```bash
docker logs -f tp-cicd-test
```

### Étape 5 : Tester

```bash
curl http://localhost:8080/actuator/health
```

### Étape 6 : Nettoyer

```bash
docker stop tp-cicd-test
docker rm tp-cicd-test
docker rmi tp-cicd-app:test
```

---

## 4️⃣ Tester les Différents Profils

### Profil Development

```bash
# Avec Maven
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Avec Java
java -jar -Dspring.profiles.active=dev target/Tp_cicd-0.0.1-SNAPSHOT.jar
```

### Profil Production

```bash
# Avec Maven
mvn spring-boot:run -Dspring-boot.run.profiles=prod

# Avec Java
java -jar -Dspring.profiles.active=prod target/Tp_cicd-0.0.1-SNAPSHOT.jar
```

---

## 5️⃣ Tester les Endpoints Actuator

### Health Check
```bash
curl http://localhost:8080/actuator/health
```

### Métriques
```bash
# Toutes les métriques
curl http://localhost:8080/actuator/metrics

# Métrique spécifique (mémoire)
curl http://localhost:8080/actuator/metrics/jvm.memory.used

# Métrique spécifique (CPU)
curl http://localhost:8080/actuator/metrics/system.cpu.usage
```

### Info
```bash
curl http://localhost:8080/actuator/info
```

---

## 6️⃣ Tester le Pipeline Complet

### Test de Push vers main

```bash
# Créer une modification
echo "Test $(date)" >> test.txt
git add test.txt
git commit -m "test: Pipeline test"
git push origin main
```

Le pipeline va :
1. ✅ Compiler l'application
2. ✅ Exécuter les tests
3. ✅ Analyser la qualité du code
4. ⚠️ Build Docker (si secrets configurés)
5. ⚠️ Déployer (si secrets configurés)

### Test de Pull Request

```bash
# Créer une branche
git checkout -b feature/test

# Faire une modification
echo "Feature test" >> test-feature.txt
git add test-feature.txt
git commit -m "feat: Add test feature"

# Pousser la branche
git push origin feature/test
```

Allez sur GitHub et créez une Pull Request vers `main`. Le pipeline va s'exécuter automatiquement !

---

## 7️⃣ Tests Automatisés

### Exécuter les tests unitaires

```bash
mvn test
```

### Exécuter les tests avec couverture

```bash
mvn verify
```

### Voir le rapport de couverture

Le rapport est généré dans : `target/site/jacoco/index.html`

---

## 8️⃣ Vérifications de Sécurité

### Vérifier que .env n'est pas commité

```bash
git check-ignore .env
# Résultat attendu : .env
```

### Vérifier qu'aucun secret n'est dans le code

```bash
git grep -i "password\s*=\s*['\"]" -- '*.yaml' '*.yml'
# Ne devrait rien retourner ou seulement des valeurs ${...}
```

### Vérifier les variables d'environnement dans l'application

```bash
docker-compose exec app env | grep DB_
```

---

## ✅ Checklist de Test

### Tests Locaux
- [ ] Application démarre avec Docker Compose
- [ ] Health check répond avec status UP
- [ ] Connexion à MySQL fonctionne
- [ ] Logs ne montrent pas d'erreurs

### Tests Pipeline
- [ ] Secret DB_PASSWORD configuré sur GitHub
- [ ] Pipeline s'exécute sur push vers main
- [ ] Build and Test passe avec succès
- [ ] Code Quality passe avec succès

### Tests Sécurité
- [ ] Fichier .env ignoré par Git
- [ ] Aucun secret hardcodé dans le code
- [ ] Variables d'environnement utilisées partout

### Tests Docker
- [ ] Image Docker se build correctement
- [ ] Conteneur démarre sans erreur
- [ ] Application accessible dans le conteneur

---

## 🐛 Dépannage

### L'application ne démarre pas

```bash
# Vérifier les logs
docker-compose logs app

# Vérifier MySQL
docker-compose logs mysql

# Redémarrer
docker-compose restart
```

### Erreur de connexion à la BD

```bash
# Vérifier que MySQL est prêt
docker-compose exec mysql mysqladmin ping -h localhost -u root -p

# Vérifier les variables
docker-compose exec app env | grep DB_
```

### Le pipeline échoue

1. Vérifiez que le secret `DB_PASSWORD` est configuré
2. Consultez les logs dans l'onglet Actions
3. Vérifiez que le code compile localement : `mvn clean install`

---

## 📊 Résultats Attendus

### Application Locale
- ✅ Status : UP
- ✅ Port : 8080
- ✅ Base de données : Connectée
- ✅ Temps de démarrage : < 60 secondes

### Pipeline GitHub
- ✅ Build : Success
- ✅ Tests : Success
- ✅ Quality : Success
- ⚠️ Docker : Nécessite secrets
- ⚠️ Deploy : Nécessite secrets

---

**Bon test ! 🚀**
