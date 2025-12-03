#!/bin/bash

# Script d'initialisation du repository Git et GitHub
# Usage: ./init-repo.sh

set -e

echo "🚀 Initialisation du repository Git"
echo ""

# Vérifier si Git est installé
if ! command -v git &> /dev/null; then
    echo "❌ Git n'est pas installé. Installez Git depuis https://git-scm.com/"
    exit 1
fi

# Vérifier si c'est déjà un repo Git
if [ -d ".git" ]; then
    echo "✅ Repository Git déjà initialisé"
else
    echo "📦 Initialisation de Git..."
    git init
    echo "✅ Git initialisé"
fi

# Demander l'URL du repository GitHub
echo ""
echo "📝 Configuration du repository GitHub"
echo "Créez d'abord un repository sur https://github.com/new"
echo ""
read -p "Entrez l'URL de votre repository GitHub (ex: https://github.com/username/tp-cicd-app.git): " repo_url

if [ -z "$repo_url" ]; then
    echo "❌ URL du repository requise"
    exit 1
fi

# Vérifier si le remote existe déjà
if git remote | grep -q "origin"; then
    echo "⚠️  Remote 'origin' existe déjà. Mise à jour..."
    git remote set-url origin "$repo_url"
else
    echo "🔗 Ajout du remote 'origin'..."
    git remote add origin "$repo_url"
fi

echo "✅ Remote configuré: $repo_url"

# Vérifier le statut Git
echo ""
echo "📊 Statut Git:"
git status --short

# Demander si on doit ajouter et commiter
echo ""
read -p "Voulez-vous ajouter et commiter tous les fichiers ? (O/N): " commit

if [ "$commit" = "O" ] || [ "$commit" = "o" ]; then
    echo "📦 Ajout des fichiers..."
    git add .
    
    echo "💾 Création du commit..."
    git commit -m "feat: Initial commit with CI/CD pipeline and Docker configuration"
    
    echo "✅ Commit créé"
fi

# Vérifier la branche actuelle
current_branch=$(git branch --show-current)

if [ "$current_branch" != "main" ]; then
    echo ""
    echo "🔄 Renommage de la branche vers 'main'..."
    git branch -M main
    echo "✅ Branche renommée en 'main'"
fi

# Demander si on doit pousser
echo ""
read -p "Voulez-vous pousser vers GitHub maintenant ? (O/N): " push

if [ "$push" = "O" ] || [ "$push" = "o" ]; then
    echo "📤 Push vers GitHub..."
    if git push -u origin main; then
        echo "✅ Code poussé vers GitHub avec succès !"
    else
        echo "❌ Erreur lors du push. Vérifiez vos credentials GitHub."
        echo "💡 Vous devrez peut-être configurer un Personal Access Token"
        echo "   Voir: https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token"
    fi
fi

# Afficher les prochaines étapes
echo ""
echo "✨ Configuration terminée !"
echo ""
echo "📋 Prochaines étapes:"
echo "1. Allez sur votre repository GitHub: $repo_url"
echo "2. Cliquez sur 'Settings' → 'Secrets and variables' → 'Actions'"
echo "3. Ajoutez les secrets requis (voir GITHUB_SETUP.md)"
echo "4. Allez dans l'onglet 'Actions' pour voir le pipeline"
echo ""
echo "📚 Documentation:"
echo "- Configuration GitHub: GITHUB_SETUP.md"
echo "- Guide de démarrage: QUICKSTART.md"
echo "- Déploiement: DEPLOYMENT.md"
echo "- Sécurité: SECURITY.md"
echo ""
echo "🎉 Bon développement !"
