#!/bin/bash

# Script de déploiement automatisé pour l'application Spring Boot
# Usage: ./deploy.sh [local|dev|prod]

set -e

ENVIRONMENT=${1:-local}
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "🚀 Déploiement de l'application en environnement: $ENVIRONMENT"

# Vérifier que le fichier .env existe
if [ ! -f "$SCRIPT_DIR/.env" ]; then
    echo "❌ Erreur: Le fichier .env n'existe pas"
    echo "📝 Copiez .env.example vers .env et configurez vos variables"
    exit 1
fi

# Charger les variables d'environnement
source "$SCRIPT_DIR/.env"

case $ENVIRONMENT in
    local)
        echo "📦 Build de l'application..."
        mvn clean package -DskipTests
        
        echo "🐳 Démarrage des conteneurs Docker..."
        docker-compose up -d
        
        echo "⏳ Attente du démarrage de l'application..."
        sleep 30
        
        echo "✅ Vérification de la santé de l'application..."
        curl -f http://localhost:${APP_PORT:-8080}/actuator/health || {
            echo "❌ L'application ne répond pas"
            docker-compose logs app
            exit 1
        }
        
        echo "✅ Déploiement local réussi!"
        echo "🌐 Application disponible sur: http://localhost:${APP_PORT:-8080}"
        ;;
        
    dev|prod)
        echo "📦 Build de l'application..."
        mvn clean package -DskipTests
        
        echo "🐳 Build de l'image Docker..."
        docker build -t ${DOCKER_USERNAME}/tp-cicd-app:${ENVIRONMENT} .
        
        echo "📤 Push de l'image vers Docker Hub..."
        docker push ${DOCKER_USERNAME}/tp-cicd-app:${ENVIRONMENT}
        
        echo "✅ Image Docker publiée avec succès!"
        echo "🔄 Connectez-vous au serveur pour déployer l'application"
        ;;
        
    *)
        echo "❌ Environnement invalide: $ENVIRONMENT"
        echo "Usage: ./deploy.sh [local|dev|prod]"
        exit 1
        ;;
esac

echo "✨ Déploiement terminé!"
