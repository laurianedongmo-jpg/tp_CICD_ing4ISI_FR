# 🚀 Test Rapide - 3 Méthodes

## ✅ Méthode 1 : Tester le Pipeline GitHub (Le Plus Simple)

### Étape 1 : Configurer le Secret

1. Allez sur : https://github.com/laurianedongmo-jpg/tp_CICD_ing4ISI_FR/settings/secrets/actions
2. Cliquez sur **"New repository secret"**
3. Ajoutez :
   - **Name** : `DB_PASSWORD`
   - **Secret** : `TestPassword123!`
4. Cliquez sur **"Add secret"**

### Étape 2 : Déclencher le Pipeline

```bash
# Faire un petit changement
echo "Test pipeline" >> README.md

# Commiter et pousser
git add README.md
git commit -m "test: Trigger pipeline"
git push origin main
```

### Étape 3 : Voir le Résultat

1. Allez sur : https://github.com/laurianedongmo-jpg/tp_CICD_ing4ISI_FR/actions
2. Cliquez sur le workflow en cours
3. Attendez que les tests passent (environ 3-5 minutes)

**Résultat attendu :**
- ✅ Build and Test : Success
- ✅ Code Quality : Success

---

## 🐳 Méthode 2 : Tester avec Docker (Si Docker est installé)

### Prérequis
- Docker Desktop installé : https://www.docker.com/products/docker-desktop

### Étapes

```bash
# 1. Créer le fichier .env
cp .env.example .env

# 2. Éditer .env (avec Notepad)
notepad .env
# Modifier DB_PASSWORD=VotreMotDePasse123!

# 3. Démarrer l'application
docker-compose up -d

# 4. Attendre 30 secondes puis tester
# Ouvrir dans le navigateur : http://localhost:8080/actuator/health

# 5. Arrêter
docker-compose down
```

---

## ☕ Méthode 3 : Tester avec Java (Si Java 21 installé)

### Prérequis
- Java 21 : https://adoptium.net/temurin/releases/?version=21
- MySQL installé et démarré

### Étapes

```bash
# 1. Vérifier Java
java -version
# Doit afficher : version "21.x.x"

# 2. Compiler (avec le wrapper Maven)
.\mvnw.cmd clean package -DskipTests

# 3. Configurer les variables d'environnement
$env:DB_URL="jdbc:mysql://localhost:3306/tp_cicd_db"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="VotreMotDePasseMySQL"

# 4. Exécuter
java -jar target/Tp_cicd-0.0.1-SNAPSHOT.jar

# 5. Tester dans un autre terminal
curl http://localhost:8080/actuator/health
```

---

## 🎯 Recommandation

**Pour tester rapidement : Utilisez la Méthode 1 (Pipeline GitHub)**

C'est la méthode la plus simple car :
- ✅ Pas besoin d'installer Docker
- ✅ Pas besoin de Java 21
- ✅ Pas besoin de MySQL local
- ✅ Tout se passe sur GitHub
- ✅ Vous voyez le pipeline en action

**Étapes minimales :**
1. Configurer le secret `DB_PASSWORD` sur GitHub
2. Faire un commit et push
3. Voir le pipeline s'exécuter dans l'onglet Actions

---

## 📊 Que Teste le Pipeline ?

Le pipeline GitHub Actions va automatiquement :

1. **Installer Java 21** ✅
2. **Démarrer MySQL 8.0** ✅
3. **Compiler l'application** ✅
4. **Exécuter les tests** ✅
5. **Vérifier la qualité du code** ✅
6. **Créer l'artifact JAR** ✅

Tout cela **sans rien installer sur votre machine** ! 🎉

---

## 🔍 Vérifier que ça Marche

### Sur GitHub Actions

Allez sur : https://github.com/laurianedongmo-jpg/tp_CICD_ing4ISI_FR/actions

Vous devriez voir :
- ✅ Workflow "CI/CD Pipeline"
- ✅ Status : Success (vert)
- ✅ Durée : ~3-5 minutes

### Cliquez sur le workflow pour voir :
- Build and Test : ✅
- Code Quality Analysis : ✅
- Build Docker : ⚠️ (nécessite secrets Docker - optionnel)
- Deploy : ⚠️ (nécessite secrets serveur - optionnel)

---

## 💡 Astuce

Pour voir les logs détaillés du pipeline :
1. Cliquez sur le workflow dans Actions
2. Cliquez sur "Build and Test"
3. Déroulez chaque étape pour voir les logs

Vous verrez :
- La compilation Maven
- L'exécution des tests
- La connexion à MySQL
- Les résultats des tests

---

**C'est tout ! Votre pipeline CI/CD est maintenant testé et fonctionnel ! 🚀**
