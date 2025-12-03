# Guide de Sécurité - Gestion des Credentials

## 🔐 Principes de Sécurité

Ce projet implémente plusieurs couches de sécurité pour protéger les credentials de la base de données et autres informations sensibles.

## 📋 Checklist de Sécurité

### ✅ À FAIRE

1. **Variables d'environnement**
   - ✅ Utiliser des variables d'environnement pour tous les secrets
   - ✅ Ne jamais hardcoder les credentials dans le code
   - ✅ Utiliser des valeurs par défaut sécurisées pour le développement

2. **Fichiers de configuration**
   - ✅ Ajouter `.env` au `.gitignore`
   - ✅ Fournir un `.env.example` avec des valeurs fictives
   - ✅ Documenter toutes les variables requises

3. **GitHub Secrets**
   - ✅ Stocker tous les secrets dans GitHub Secrets
   - ✅ Ne jamais logger les secrets dans les workflows
   - ✅ Utiliser des environnements protégés pour la production

4. **Docker**
   - ✅ Ne pas inclure de secrets dans les images Docker
   - ✅ Passer les secrets via variables d'environnement au runtime
   - ✅ Utiliser des utilisateurs non-root dans les conteneurs

5. **Base de données**
   - ✅ Utiliser des mots de passe forts
   - ✅ Changer les credentials par défaut
   - ✅ Limiter les privilèges des utilisateurs

### ❌ À NE PAS FAIRE

- ❌ Commiter le fichier `.env` avec des vraies valeurs
- ❌ Hardcoder les mots de passe dans le code
- ❌ Utiliser les mêmes credentials en dev et prod
- ❌ Partager les secrets par email ou chat
- ❌ Logger les credentials dans les logs
- ❌ Inclure les secrets dans les images Docker

## 🔑 Gestion des Secrets par Environnement

### Développement Local

```bash
# Copier le template
cp .env.example .env

# Éditer avec vos valeurs locales
nano .env

# Le fichier .env est automatiquement ignoré par Git
```

### CI/CD (GitHub Actions)

1. Aller dans `Settings → Secrets and variables → Actions`
2. Ajouter les secrets nécessaires :
   - `DB_PASSWORD`
   - `DOCKER_USERNAME`
   - `DOCKER_PASSWORD`
   - etc.

### Production (Docker Compose)

```bash
# Sur le serveur de production
cd /opt/tp-cicd-app

# Créer le fichier .env avec les vraies valeurs
cat > .env << EOF
DB_NAME=tp_cicd_db
DB_USERNAME=tp_user
DB_PASSWORD=VotreMdpSecurise123!
DOCKER_USERNAME=votre_username
EOF

# Protéger le fichier
chmod 600 .env
```

### Production (Kubernetes)

```bash
# Créer les secrets Kubernetes
kubectl create secret generic mysql-secret \
  --from-literal=mysql-root-password='VotreMdpSecurise123!' \
  --from-literal=mysql-password='VotreMdpSecurise123!' \
  -n tp-cicd

# Vérifier que le secret est créé (les valeurs sont masquées)
kubectl get secrets -n tp-cicd
```

## 🛡️ Bonnes Pratiques

### 1. Mots de Passe Forts

```bash
# Générer un mot de passe fort
openssl rand -base64 32

# Ou avec pwgen
pwgen -s 32 1
```

### 2. Rotation des Secrets

- Changer les mots de passe tous les 90 jours
- Mettre à jour les secrets dans tous les environnements
- Documenter les changements

### 3. Accès Limité

- Limiter l'accès aux secrets aux personnes nécessaires
- Utiliser des rôles et permissions appropriés
- Auditer régulièrement les accès

### 4. Chiffrement

- Utiliser TLS/SSL pour les connexions à la base de données
- Chiffrer les volumes de données sensibles
- Utiliser HTTPS pour toutes les communications

### 5. Monitoring

```yaml
# Activer les logs d'audit
logging:
  level:
    org.springframework.security: DEBUG
```

## 🔍 Vérification de Sécurité

### Vérifier qu'aucun secret n'est commité

```bash
# Rechercher des patterns de secrets
git grep -i "password\s*=\s*['\"]" -- '*.yaml' '*.yml' '*.properties'
git grep -i "secret\s*=\s*['\"]" -- '*.yaml' '*.yml' '*.properties'

# Utiliser git-secrets (recommandé)
git secrets --scan
```

### Vérifier les variables d'environnement

```bash
# Dans application.yaml, toutes les valeurs sensibles doivent utiliser ${VAR}
grep -r "password:" src/main/resources/
# Résultat attendu: password: ${DB_PASSWORD:password}
```

### Vérifier le .gitignore

```bash
# S'assurer que .env est ignoré
git check-ignore .env
# Résultat attendu: .env
```

## 🚨 En Cas de Fuite de Secrets

### Actions Immédiates

1. **Révoquer immédiatement** les credentials compromis
2. **Changer tous les mots de passe** affectés
3. **Mettre à jour** tous les environnements
4. **Auditer** les accès récents
5. **Notifier** l'équipe de sécurité

### Nettoyage Git

```bash
# Si un secret a été commité par erreur
# Utiliser BFG Repo-Cleaner ou git-filter-branch

# Exemple avec BFG
bfg --replace-text passwords.txt
git reflog expire --expire=now --all
git gc --prune=now --aggressive
```

## 📚 Ressources

- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [GitHub Secrets Documentation](https://docs.github.com/en/actions/security-guides/encrypted-secrets)
- [Docker Secrets](https://docs.docker.com/engine/swarm/secrets/)
- [Kubernetes Secrets](https://kubernetes.io/docs/concepts/configuration/secret/)

## 📞 Contact Sécurité

En cas de découverte d'une vulnérabilité de sécurité, veuillez contacter l'équipe de sécurité immédiatement.

---

**Rappel Important** : La sécurité est la responsabilité de tous. Suivez toujours ces pratiques et signalez toute anomalie.
