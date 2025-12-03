# 🔧 Configuration GitHub pour le Pipeline CI/CD

## 📋 Secrets Requis

Avant que le pipeline fonctionne, vous devez configurer les secrets suivants dans GitHub.

### Accéder aux Secrets

1. Allez sur votre repository GitHub
2. Cliquez sur **Settings** (en haut à droite)
3. Dans le menu de gauche : **Secrets and variables** → **Actions**
4. Cliquez sur **New repository secret**

### Secrets à Configurer

#### 1. DB_PASSWORD (Obligatoire)
- **Nom** : `DB_PASSWORD`
- **Valeur** : Votre mot de passe MySQL (ex: `MySecurePassword123!`)
- **Utilisé pour** : Tests et déploiement de la base de données

#### 2. DOCKER_USERNAME (Obligatoire pour build Docker)
- **Nom** : `DOCKER_USERNAME`
- **Valeur** : Votre nom d'utilisateur Docker Hub
- **Utilisé pour** : Publier l'image Docker

#### 3. DOCKER_PASSWORD (Obligatoire pour build Docker)
- **Nom** : `DOCKER_PASSWORD`
- **Valeur** : Votre token d'accès Docker Hub
- **Comment obtenir** :
  1. Allez sur https://hub.docker.com/settings/security
  2. Cliquez sur "New Access Token"
  3. Donnez un nom (ex: "GitHub Actions")
  4. Copiez le token généré

#### 4. Secrets de Déploiement (Optionnels - pour déploiement automatique)

**SERVER_HOST**
- **Nom** : `SERVER_HOST`
- **Valeur** : Adresse IP ou domaine de votre serveur (ex: `192.168.1.100` ou `app.example.com`)

**SERVER_USERNAME**
- **Nom** : `SERVER_USERNAME`
- **Valeur** : Nom d'utilisateur SSH (ex: `ubuntu` ou `root`)

**SERVER_PORT**
- **Nom** : `SERVER_PORT`
- **Valeur** : Port SSH (généralement `22`)

**SSH_PRIVATE_KEY**
- **Nom** : `SSH_PRIVATE_KEY`
- **Valeur** : Votre clé privée SSH complète
- **Comment obtenir** :
  ```bash
  # Générer une nouvelle paire de clés (si nécessaire)
  ssh-keygen -t ed25519 -C "github-actions" -f ~/.ssh/github_actions
  
  # Afficher la clé privée (à copier dans GitHub)
  cat ~/.ssh/github_actions
  
  # Copier la clé publique sur le serveur
  ssh-copy-id -i ~/.ssh/github_actions.pub user@server
  ```

**APP_URL**
- **Nom** : `APP_URL`
- **Valeur** : URL complète de votre application (ex: `http://app.example.com`)

## 🚀 Initialiser le Repository

### Commandes Git

```bash
# 1. Initialiser Git (si pas déjà fait)
git init

# 2. Ajouter tous les fichiers
git add .

# 3. Créer le premier commit
git commit -m "feat: Initial commit with CI/CD pipeline"

# 4. Créer la branche main
git branch -M main

# 5. Ajouter le remote (remplacer par votre URL)
git remote add origin https://github.com/VOTRE_USERNAME/tp-cicd-app.git

# 6. Pousser vers GitHub
git push -u origin main
```

## 📊 Voir le Pipeline en Action

Une fois le code poussé et les secrets configurés :

1. Allez sur votre repository GitHub
2. Cliquez sur l'onglet **Actions**
3. Vous verrez le workflow "CI/CD Pipeline" en cours d'exécution

### Déclencheurs du Pipeline

Le pipeline s'exécute automatiquement sur :
- ✅ Push vers `main` ou `develop`
- ✅ Pull Request vers `main` ou `develop`

## 🔍 Vérifier la Configuration

### Tester sans Docker Hub (développement)

Si vous voulez tester le pipeline sans publier sur Docker Hub, modifiez `.github/workflows/ci-cd.yml` :

```yaml
build-docker:
  name: Build Docker Image
  runs-on: ubuntu-latest
  needs: [build-and-test, code-quality]
  if: false  # ← Désactiver temporairement
```

### Tester sans Déploiement

Pour désactiver le déploiement automatique :

```yaml
deploy:
  name: Deploy to Production
  runs-on: ubuntu-latest
  needs: build-docker
  if: false  # ← Désactiver temporairement
```

## 🎯 Configuration Minimale pour Démarrer

Pour faire fonctionner le pipeline avec les fonctionnalités de base :

### Secrets Minimaux
1. `DB_PASSWORD` - Pour les tests

### Pour Build Docker (optionnel)
2. `DOCKER_USERNAME`
3. `DOCKER_PASSWORD`

### Pour Déploiement (optionnel)
4. `SERVER_HOST`
5. `SERVER_USERNAME`
6. `SSH_PRIVATE_KEY`
7. `SERVER_PORT`
8. `APP_URL`

## 📝 Exemple de Configuration Complète

```bash
# 1. Configurer les secrets GitHub (via l'interface web)
DB_PASSWORD=MySecurePassword123!
DOCKER_USERNAME=monusername
DOCKER_PASSWORD=dckr_pat_xxxxxxxxxxxxx

# 2. Pousser le code
git push origin main

# 3. Vérifier dans Actions
# Le pipeline devrait s'exécuter automatiquement
```

## 🐛 Dépannage

### Le pipeline ne démarre pas
- Vérifiez que le fichier `.github/workflows/ci-cd.yml` existe
- Vérifiez que vous avez poussé vers `main` ou `develop`
- Vérifiez dans l'onglet Actions si les workflows sont activés

### Erreur "Secret not found"
- Vérifiez que tous les secrets requis sont configurés
- Les noms des secrets sont sensibles à la casse
- Relancez le workflow après avoir ajouté les secrets

### Erreur de connexion Docker Hub
- Vérifiez que `DOCKER_USERNAME` est correct
- Utilisez un token d'accès, pas votre mot de passe
- Vérifiez que le token a les permissions nécessaires

### Erreur de déploiement SSH
- Vérifiez que la clé SSH est correcte (incluant BEGIN et END)
- Vérifiez que la clé publique est sur le serveur
- Testez la connexion SSH manuellement

## ✅ Checklist de Configuration

- [ ] Repository GitHub créé
- [ ] Code poussé vers GitHub
- [ ] Secret `DB_PASSWORD` configuré
- [ ] Secret `DOCKER_USERNAME` configuré (si build Docker)
- [ ] Secret `DOCKER_PASSWORD` configuré (si build Docker)
- [ ] Secrets de déploiement configurés (si déploiement auto)
- [ ] Pipeline visible dans l'onglet Actions
- [ ] Premier workflow exécuté avec succès

## 🎉 Prochaines Étapes

Une fois le pipeline configuré :
1. Créez une branche `develop` pour le développement
2. Utilisez des Pull Requests pour merger vers `main`
3. Le pipeline testera automatiquement chaque PR
4. Le déploiement se fera automatiquement sur `main`

---

**Besoin d'aide ?** Consultez la [documentation GitHub Actions](https://docs.github.com/en/actions)
