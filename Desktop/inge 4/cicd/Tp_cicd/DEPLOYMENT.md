# Guide de Déploiement - Application Spring Boot CI/CD

## 📋 Prérequis

- Java 21
- Maven 3.8+
- Docker & Docker Compose
- Compte GitHub
- Compte Docker Hub (optionnel)
- Serveur de déploiement avec SSH (optionnel)

## 🔐 Configuration des Secrets

### Secrets GitHub (Settings → Secrets and variables → Actions)

Configurez les secrets suivants dans votre repository GitHub :

#### Secrets de Base de Données
- `DB_PASSWORD` : Mot de passe de la base de données MySQL

#### Secrets Docker (si vous utilisez Docker Hub)
- `DOCKER_USERNAME` : Nom d'utilisateur Docker Hub
- `DOCKER_PASSWORD` : Token d'accès Docker Hub

#### Secrets de Déploiement (pour le déploiement automatique)
- `SERVER_HOST` : Adresse IP ou domaine du serveur
- `SERVER_USERNAME` : Nom d'utilisateur SSH
- `SERVER_PORT` : Port SSH (généralement 22)
- `SSH_PRIVATE_KEY` : Clé privée SSH pour l'authentification
- `APP_URL` : URL de l'application déployée

## 🚀 Déploiement Local

### 1. Configuration de l'environnement

```bash
# Copier le fichier d'exemple
cp .env.example .env

# Éditer .env avec vos valeurs
# IMPORTANT: Ne jamais commiter le fichier .env !
```

### 2. Démarrer avec Docker Compose

```bash
# Build et démarrage
docker-compose up -d

# Vérifier les logs
docker-compose logs -f app

# Arrêter les services
docker-compose down
```

### 3. Build et exécution manuelle

```bash
# Build l'application
mvn clean package -DskipTests

# Exécuter avec les variables d'environnement
java -jar target/Tp_cicd-0.0.1-SNAPSHOT.jar \
  --spring.datasource.url=${DB_URL} \
  --spring.datasource.username=${DB_USERNAME} \
  --spring.datasource.password=${DB_PASSWORD}
```

## 🔄 Pipeline CI/CD

Le pipeline s'exécute automatiquement sur :
- Push vers `main` ou `develop`
- Pull requests vers `main` ou `develop`

### Étapes du Pipeline

1. **Build and Test** : Compilation et tests avec MySQL
2. **Code Quality** : Analyse de la qualité du code
3. **Build Docker** : Construction de l'image Docker (branche main uniquement)
4. **Deploy** : Déploiement automatique (branche main uniquement)

## 🐳 Build Docker Manuel

```bash
# Build l'application
mvn clean package -DskipTests

# Build l'image Docker
docker build -t tp-cicd-app:latest .

# Exécuter le conteneur
docker run -d \
  -p 8080:8080 \
  -e DB_URL=jdbc:mysql://host.docker.internal:3306/tp_cicd_db \
  -e DB_USERNAME=root \
  -e DB_PASSWORD=your_password \
  --name tp-cicd-app \
  tp-cicd-app:latest
```

## 🖥️ Déploiement sur Serveur

### Configuration du serveur

1. Installer Docker et Docker Compose sur le serveur
2. Créer le répertoire de déploiement :

```bash
sudo mkdir -p /opt/tp-cicd-app
cd /opt/tp-cicd-app
```

3. Copier les fichiers nécessaires :
   - `docker-compose.yml`
   - `.env` (avec les vraies valeurs)

4. Configurer les secrets GitHub pour le déploiement automatique

### Déploiement manuel sur serveur

```bash
# Se connecter au serveur
ssh user@your-server

# Aller dans le répertoire
cd /opt/tp-cicd-app

# Pull la dernière image
docker-compose pull

# Redémarrer les services
docker-compose down
docker-compose up -d

# Vérifier les logs
docker-compose logs -f
```

## 🔍 Vérification du Déploiement

```bash
# Vérifier la santé de l'application
curl http://localhost:8080/actuator/health

# Vérifier les logs
docker-compose logs -f app

# Vérifier les conteneurs en cours d'exécution
docker-compose ps
```

## 🛡️ Sécurité

### Bonnes Pratiques

1. **Ne jamais commiter les secrets** :
   - Ajouter `.env` au `.gitignore`
   - Utiliser GitHub Secrets pour le CI/CD

2. **Rotation des mots de passe** :
   - Changer régulièrement les mots de passe de la BD
   - Mettre à jour les secrets GitHub

3. **Accès SSH sécurisé** :
   - Utiliser des clés SSH au lieu de mots de passe
   - Limiter les permissions de la clé privée

4. **Variables d'environnement** :
   - Toujours utiliser des variables d'environnement pour les secrets
   - Ne jamais hardcoder les credentials

## 📊 Monitoring

### Endpoints disponibles

- Health check : `http://localhost:8080/actuator/health`
- Métriques : `http://localhost:8080/actuator/metrics`
- Info : `http://localhost:8080/actuator/info`

## 🐛 Dépannage

### L'application ne démarre pas

```bash
# Vérifier les logs
docker-compose logs app

# Vérifier la connexion à la BD
docker-compose logs mysql

# Redémarrer les services
docker-compose restart
```

### Problèmes de connexion à la BD

```bash
# Vérifier que MySQL est prêt
docker-compose exec mysql mysqladmin ping -h localhost -u root -p

# Vérifier les variables d'environnement
docker-compose exec app env | grep DB_
```

## 📝 Notes

- Le pipeline crée automatiquement une image Docker taggée avec le SHA du commit
- Les artifacts sont conservés pendant 5 jours
- Le déploiement automatique nécessite un environnement "production" configuré dans GitHub
