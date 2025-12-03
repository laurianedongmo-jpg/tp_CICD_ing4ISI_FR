# Script de déploiement automatisé pour l'application Spring Boot (Windows)
# Usage: .\deploy.ps1 [local|dev|prod]

param(
    [Parameter(Mandatory=$false)]
    [ValidateSet('local','dev','prod')]
    [string]$Environment = 'local'
)

$ErrorActionPreference = "Stop"

Write-Host "🚀 Déploiement de l'application en environnement: $Environment" -ForegroundColor Green

# Vérifier que le fichier .env existe
if (-not (Test-Path ".env")) {
    Write-Host "❌ Erreur: Le fichier .env n'existe pas" -ForegroundColor Red
    Write-Host "📝 Copiez .env.example vers .env et configurez vos variables" -ForegroundColor Yellow
    exit 1
}

# Charger les variables d'environnement
Get-Content .env | ForEach-Object {
    if ($_ -match '^([^#][^=]+)=(.*)$') {
        $name = $matches[1].Trim()
        $value = $matches[2].Trim()
        Set-Item -Path "env:$name" -Value $value
    }
}

switch ($Environment) {
    'local' {
        Write-Host "📦 Build de l'application..." -ForegroundColor Cyan
        mvn clean package -DskipTests
        
        Write-Host "🐳 Démarrage des conteneurs Docker..." -ForegroundColor Cyan
        docker-compose up -d
        
        Write-Host "⏳ Attente du démarrage de l'application..." -ForegroundColor Yellow
        Start-Sleep -Seconds 30
        
        Write-Host "✅ Vérification de la santé de l'application..." -ForegroundColor Cyan
        $appPort = if ($env:APP_PORT) { $env:APP_PORT } else { "8080" }
        try {
            $response = Invoke-WebRequest -Uri "http://localhost:$appPort/actuator/health" -UseBasicParsing
            Write-Host "✅ Déploiement local réussi!" -ForegroundColor Green
            Write-Host "🌐 Application disponible sur: http://localhost:$appPort" -ForegroundColor Green
        }
        catch {
            Write-Host "❌ L'application ne répond pas" -ForegroundColor Red
            docker-compose logs app
            exit 1
        }
    }
    
    { $_ -in 'dev','prod' } {
        Write-Host "📦 Build de l'application..." -ForegroundColor Cyan
        mvn clean package -DskipTests
        
        Write-Host "🐳 Build de l'image Docker..." -ForegroundColor Cyan
        docker build -t "$env:DOCKER_USERNAME/tp-cicd-app:$Environment" .
        
        Write-Host "📤 Push de l'image vers Docker Hub..." -ForegroundColor Cyan
        docker push "$env:DOCKER_USERNAME/tp-cicd-app:$Environment"
        
        Write-Host "✅ Image Docker publiée avec succès!" -ForegroundColor Green
        Write-Host "🔄 Connectez-vous au serveur pour déployer l'application" -ForegroundColor Yellow
    }
}

Write-Host "✨ Déploiement terminé!" -ForegroundColor Green
