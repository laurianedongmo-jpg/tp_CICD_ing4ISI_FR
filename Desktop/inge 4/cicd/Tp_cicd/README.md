# TP CI/CD - Application Spring Boot

Application Spring Boot avec pipeline CI/CD automatisé et déploiement sécurisé avec base de données MySQL.

## 🚀 Fonctionnalités

- ✅ Application Spring Boot 4.0 avec Java 21
- ✅ Base de données MySQL avec JPA/Hibernate
- ✅ Pipeline CI/CD avec GitHub Actions
- ✅ Containerisation avec Docker
- ✅ Gestion sécurisée des credentials
- ✅ Déploiement automatisé
- ✅ Health checks et monitoring avec Actuator

## 📦 Technologies

- **Backend**: Spring Boot 4.0, Spring Data JPA
- **Base de données**: MySQL 8.0
- **Build**: Maven
- **Containerisation**: Docker, Docker Compose
- **CI/CD**: GitHub Actions
- **Monitoring**: Spring Boot Actuator

## 🔧 Configuration Rapide

### 1. Cloner le repository

```bash
git clone <votre-repo>
cd Tp_cicd
```

### 2. Configurer les variables d'environnement

```bash
cp .env.example .env
# Éditer .env avec vos valeurs
```

### 3. Démarrer l'application

```bash
# Avec Docker Compose (recommandé)
docker-compose up -d

# Ou avec Maven
mvn spring-boot:run
```

### 4. Vérifier le déploiement

```bash
curl http://localhost:8080/actuator/health
```

## 🔐 Sécurité des Credentials

Les credentials de base de données sont gérés de manière sécurisée via :

1. **Variables d'environnement** : Jamais hardcodées dans le code
2. **Fichier .env** : Exclu du contrôle de version (.gitignore)
3. **GitHub Secrets** : Pour le pipeline CI/CD
4. **Docker secrets** : Pour le déploiement en production

### Configuration des Secrets GitHub

Allez dans `Settings → Secrets and variables → Actions` et ajoutez :

- `DB_PASSWORD` : Mot de passe MySQL
- `DOCKER_USERNAME` : Nom d'utilisateur Docker Hub
- `DOCKER_PASSWORD` : Token Docker Hub
- `SERVER_HOST` : Adresse du serveur de déploiement
- `SERVER_USERNAME` : Utilisateur SSH
- `SSH_PRIVATE_KEY` : Clé privée SSH
- `SERVER_PORT` : Port SSH (22)
- `APP_URL` : URL de l'application

## 📚 Documentation

Consultez [DEPLOYMENT.md](DEPLOYMENT.md) pour :
- Guide de déploiement complet
- Configuration du pipeline CI/CD
- Déploiement sur serveur
- Dépannage

## 🔄 Pipeline CI/CD

Le pipeline s'exécute automatiquement et comprend :

1. **Build and Test** : Compilation et tests unitaires
2. **Code Quality** : Analyse de la qualité du code
3. **Build Docker** : Construction de l'image Docker
4. **Deploy** : Déploiement automatique en production

## 📊 Endpoints

- Application : `http://localhost:8080`
- Health check : `http://localhost:8080/actuator/health`
- Métriques : `http://localhost:8080/actuator/metrics`
- Info : `http://localhost:8080/actuator/info`

## 🛠️ Commandes Utiles

```bash
# Build l'application
mvn clean package

# Exécuter les tests
mvn test

# Build l'image Docker
docker build -t tp-cicd-app .

# Voir les logs
docker-compose logs -f

# Arrêter les services
docker-compose down

# Nettoyer les volumes
docker-compose down -v
```

## 📝 Structure du Projet

```
.
├── .github/
│   └── workflows/
│       └── ci-cd.yml          # Pipeline CI/CD
├── src/
│   ├── main/
│   │   ├── java/              # Code source
│   │   └── resources/
│   │       └── application.yaml  # Configuration
│   └── test/                  # Tests
├── init-scripts/
│   └── init.sql               # Script d'initialisation BD
├── Dockerfile                 # Image Docker
├── docker-compose.yml         # Orchestration
├── .env.example               # Template variables d'environnement
├── .dockerignore              # Exclusions Docker
├── pom.xml                    # Configuration Maven
└── DEPLOYMENT.md              # Guide de déploiement

```

## 🤝 Contribution

1. Fork le projet
2. Créer une branche (`git checkout -b feature/AmazingFeature`)
3. Commit les changements (`git commit -m 'Add AmazingFeature'`)
4. Push vers la branche (`git push origin feature/AmazingFeature`)
5. Ouvrir une Pull Request

## 📄 Licence

Ce projet est sous licence MIT.

## 👥 Auteurs

TP CI/CD - Implémentation d'un pipeline

## 🆘 Support

Pour toute question ou problème, consultez [DEPLOYMENT.md](DEPLOYMENT.md) ou ouvrez une issue.
