# 🚀 Guide de Démarrage Rapide

Ce guide vous permet de démarrer l'application en moins de 5 minutes.

## Prérequis

- Docker et Docker Compose installés
- Java 21 (optionnel, pour le développement)
- Maven (optionnel, pour le développement)

## Étapes de Démarrage

### 1. Configurer les Variables d'Environnement

```bash
# Copier le fichier d'exemple
cp .env.example .env

# Éditer le fichier .env (Windows)
notepad .env

# Ou sur Linux/Mac
nano .env
```

Modifier au minimum ces valeurs :
```env
DB_PASSWORD=VotreMotDePasseSecurise123!
DB_USERNAME=tp_user
DOCKER_USERNAME=votre_username_docker
```

### 2. Démarrer l'Application

#### Option A : Avec Docker Compose (Recommandé)

```bash
# Build et démarrage
docker-compose up -d

# Voir les logs
docker-compose logs -f
```

#### Option B : Avec les Scripts de Déploiement

**Windows :**
```powershell
.\deploy.ps1 local
```

**Linux/Mac :**
```bash
chmod +x deploy.sh
./deploy.sh local
```

### 3. Vérifier le Déploiement

Ouvrir votre navigateur et accéder à :

- **Application** : http://localhost:8080
- **Health Check** : http://localhost:8080/actuator/health
- **Métriques** : http://localhost:8080/actuator/metrics

Ou avec curl :
```bash
curl http://localhost:8080/actuator/health
```

Réponse attendue :
```json
{
  "status": "UP"
}
```

## 🎯 Commandes Utiles

### Voir les Logs

```bash
# Tous les services
docker-compose logs -f

# Application uniquement
docker-compose logs -f app

# MySQL uniquement
docker-compose logs -f mysql
```

### Redémarrer l'Application

```bash
docker-compose restart app
```

### Arrêter l'Application

```bash
# Arrêter sans supprimer les données
docker-compose stop

# Arrêter et supprimer les conteneurs
docker-compose down

# Arrêter et supprimer les données
docker-compose down -v
```

### Accéder à la Base de Données

```bash
# Se connecter à MySQL
docker-compose exec mysql mysql -u root -p

# Entrer le mot de passe défini dans .env (DB_PASSWORD)
```

## 🔧 Développement

### Build Local

```bash
# Compiler l'application
mvn clean package

# Exécuter les tests
mvn test

# Exécuter l'application
mvn spring-boot:run
```

### Hot Reload

Pour le développement avec rechargement automatique :

```bash
# Ajouter spring-boot-devtools dans pom.xml (déjà inclus)
mvn spring-boot:run
```

## 🐛 Dépannage

### L'application ne démarre pas

```bash
# Vérifier les logs
docker-compose logs app

# Vérifier que MySQL est prêt
docker-compose ps
```

### Erreur de connexion à la base de données

```bash
# Vérifier que MySQL est démarré
docker-compose ps mysql

# Vérifier les variables d'environnement
docker-compose exec app env | grep DB_

# Redémarrer MySQL
docker-compose restart mysql
```

### Port déjà utilisé

```bash
# Changer le port dans .env
APP_PORT=8081

# Redémarrer
docker-compose down
docker-compose up -d
```

### Nettoyer complètement

```bash
# Supprimer tous les conteneurs et volumes
docker-compose down -v

# Supprimer les images
docker rmi $(docker images 'tp-cicd*' -q)

# Redémarrer proprement
docker-compose up -d
```

## 📚 Prochaines Étapes

1. **Configurer le CI/CD** : Voir [DEPLOYMENT.md](DEPLOYMENT.md)
2. **Sécuriser l'application** : Voir [SECURITY.md](SECURITY.md)
3. **Développer des fonctionnalités** : Voir [README.md](README.md)

## 🆘 Besoin d'Aide ?

- Consultez [DEPLOYMENT.md](DEPLOYMENT.md) pour plus de détails
- Consultez [SECURITY.md](SECURITY.md) pour la gestion des secrets
- Ouvrez une issue sur GitHub

## ✅ Checklist de Vérification

- [ ] Docker et Docker Compose installés
- [ ] Fichier .env créé et configuré
- [ ] Application démarrée avec `docker-compose up -d`
- [ ] Health check répond : http://localhost:8080/actuator/health
- [ ] Logs sans erreur : `docker-compose logs app`
- [ ] Base de données accessible

Félicitations ! Votre application est maintenant opérationnelle ! 🎉
