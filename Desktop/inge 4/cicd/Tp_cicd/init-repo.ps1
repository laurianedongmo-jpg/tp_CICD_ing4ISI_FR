# Script d'initialisation du repository Git et GitHub
# Usage: .\init-repo.ps1

Write-Host "🚀 Initialisation du repository Git" -ForegroundColor Green
Write-Host ""

# Vérifier si Git est installé
try {
    git --version | Out-Null
} catch {
    Write-Host "❌ Git n'est pas installé. Installez Git depuis https://git-scm.com/" -ForegroundColor Red
    exit 1
}

# Vérifier si c'est déjà un repo Git
if (Test-Path ".git") {
    Write-Host "✅ Repository Git déjà initialisé" -ForegroundColor Yellow
} else {
    Write-Host "📦 Initialisation de Git..." -ForegroundColor Cyan
    git init
    Write-Host "✅ Git initialisé" -ForegroundColor Green
}

# Demander l'URL du repository GitHub
Write-Host ""
Write-Host "📝 Configuration du repository GitHub" -ForegroundColor Cyan
Write-Host "Créez d'abord un repository sur https://github.com/new" -ForegroundColor Yellow
Write-Host ""
$repoUrl = Read-Host "Entrez l'URL de votre repository GitHub (ex: https://github.com/username/tp-cicd-app.git)"

if ([string]::IsNullOrWhiteSpace($repoUrl)) {
    Write-Host "❌ URL du repository requise" -ForegroundColor Red
    exit 1
}

# Vérifier si le remote existe déjà
$remoteExists = git remote | Select-String -Pattern "origin" -Quiet

if ($remoteExists) {
    Write-Host "⚠️  Remote 'origin' existe déjà. Mise à jour..." -ForegroundColor Yellow
    git remote set-url origin $repoUrl
} else {
    Write-Host "🔗 Ajout du remote 'origin'..." -ForegroundColor Cyan
    git remote add origin $repoUrl
}

Write-Host "✅ Remote configuré: $repoUrl" -ForegroundColor Green

# Vérifier le statut Git
Write-Host ""
Write-Host "📊 Statut Git:" -ForegroundColor Cyan
git status --short

# Demander si on doit ajouter et commiter
Write-Host ""
$commit = Read-Host "Voulez-vous ajouter et commiter tous les fichiers ? (O/N)"

if ($commit -eq "O" -or $commit -eq "o") {
    Write-Host "📦 Ajout des fichiers..." -ForegroundColor Cyan
    git add .
    
    Write-Host "💾 Création du commit..." -ForegroundColor Cyan
    git commit -m "feat: Initial commit with CI/CD pipeline and Docker configuration"
    
    Write-Host "✅ Commit créé" -ForegroundColor Green
}

# Vérifier la branche actuelle
$currentBranch = git branch --show-current

if ($currentBranch -ne "main") {
    Write-Host ""
    Write-Host "🔄 Renommage de la branche vers 'main'..." -ForegroundColor Cyan
    git branch -M main
    Write-Host "✅ Branche renommée en 'main'" -ForegroundColor Green
}

# Demander si on doit pousser
Write-Host ""
$push = Read-Host "Voulez-vous pousser vers GitHub maintenant ? (O/N)"

if ($push -eq "O" -or $push -eq "o") {
    Write-Host "📤 Push vers GitHub..." -ForegroundColor Cyan
    try {
        git push -u origin main
        Write-Host "✅ Code poussé vers GitHub avec succès !" -ForegroundColor Green
    } catch {
        Write-Host "❌ Erreur lors du push. Vérifiez vos credentials GitHub." -ForegroundColor Red
        Write-Host "💡 Vous devrez peut-être configurer un Personal Access Token" -ForegroundColor Yellow
        Write-Host "   Voir: https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token" -ForegroundColor Yellow
    }
}

# Afficher les prochaines étapes
Write-Host ""
Write-Host "✨ Configuration terminée !" -ForegroundColor Green
Write-Host ""
Write-Host "📋 Prochaines étapes:" -ForegroundColor Cyan
Write-Host "1. Allez sur votre repository GitHub: $repoUrl" -ForegroundColor White
Write-Host "2. Cliquez sur 'Settings' → 'Secrets and variables' → 'Actions'" -ForegroundColor White
Write-Host "3. Ajoutez les secrets requis (voir GITHUB_SETUP.md)" -ForegroundColor White
Write-Host "4. Allez dans l'onglet 'Actions' pour voir le pipeline" -ForegroundColor White
Write-Host ""
Write-Host "📚 Documentation:" -ForegroundColor Cyan
Write-Host "- Configuration GitHub: GITHUB_SETUP.md" -ForegroundColor White
Write-Host "- Guide de démarrage: QUICKSTART.md" -ForegroundColor White
Write-Host "- Déploiement: DEPLOYMENT.md" -ForegroundColor White
Write-Host "- Sécurité: SECURITY.md" -ForegroundColor White
Write-Host ""
Write-Host "🎉 Bon développement !" -ForegroundColor Green
