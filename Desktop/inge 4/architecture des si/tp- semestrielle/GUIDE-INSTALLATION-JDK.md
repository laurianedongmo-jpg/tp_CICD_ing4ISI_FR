# 📦 Guide d'installation Java JDK 17

## Méthode 1 : Installation automatique avec Chocolatey (Recommandé)

### Étape 1 : Installer Chocolatey (si pas déjà installé)

Ouvrir PowerShell en **mode Administrateur** et exécuter :

```powershell
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))
```

### Étape 2 : Installer Java JDK 17

Dans PowerShell (mode Administrateur) :

```powershell
choco install openjdk17 -y
```

### Étape 3 : Vérifier l'installation

Fermer et rouvrir PowerShell, puis :

```bash
java -version
```

Vous devriez voir : `openjdk version "17.x.x"`

---

## Méthode 2 : Installation manuelle (Alternative)

### Étape 1 : Télécharger Java JDK 17

Aller sur : **https://adoptium.net/**

1. Cliquer sur "Download"
2. Choisir :
   - Version : **17 - LTS**
   - Operating System : **Windows**
   - Architecture : **x64**
3. Télécharger le fichier `.msi`

### Étape 2 : Installer

1. Double-cliquer sur le fichier téléchargé
2. Suivre l'assistant d'installation
3. **Important** : Cocher "Set JAVA_HOME variable"
4. **Important** : Cocher "Add to PATH"
5. Cliquer sur "Install"

### Étape 3 : Vérifier l'installation

Ouvrir un **nouveau** terminal (PowerShell ou CMD) :

```bash
java -version
```

### Étape 4 : Configurer JAVA_HOME (si nécessaire)

Si `java -version` ne fonctionne pas :

1. Ouvrir "Panneau de configuration"
2. Système → Paramètres système avancés
3. Variables d'environnement
4. Variables système → Nouveau :
   - Nom : `JAVA_HOME`
   - Valeur : `C:\Program Files\Eclipse Adoptium\jdk-17.x.x-hotspot`
5. Modifier la variable `Path` :
   - Ajouter : `%JAVA_HOME%\bin`
6. OK → OK → OK
7. **Redémarrer le terminal**

---

## Méthode 3 : Installation avec WinGet (Windows 11)

```powershell
winget install EclipseAdoptium.Temurin.17.JDK
```

---

## Vérification complète

Après installation, vérifier :

```bash
# Version de Java
java -version

# Compilateur Java
javac -version

# Variable JAVA_HOME
echo %JAVA_HOME%
```

**Résultat attendu :**
```
openjdk version "17.0.x" 2024-xx-xx
OpenJDK Runtime Environment Temurin-17.0.x (build 17.0.x+x)
OpenJDK 64-Bit Server VM Temurin-17.0.x (build 17.0.x+x, mixed mode, sharing)
```

---

## Installation de Maven

### Avec Chocolatey (Recommandé)

```powershell
choco install maven -y
```

### Vérification

```bash
mvn -version
```

---

## Après installation

Une fois Java et Maven installés :

```bash
# 1. Aller dans le dossier du projet
cd "C:\Users\hp\Desktop\inge 4\architecture des si\tp- semestrielle"

# 2. Démarrer les services
start-services.bat
```

---

## Problèmes courants

### Problème : "java n'est pas reconnu"

**Solution :**
1. Redémarrer le terminal
2. Vérifier que Java est dans le PATH
3. Réinstaller en cochant "Add to PATH"

### Problème : "JAVA_HOME n'est pas défini"

**Solution :**
```powershell
# PowerShell
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.9.9-hotspot"
$env:Path += ";$env:JAVA_HOME\bin"
```

### Problème : Mauvaise version de Java

**Solution :**
```bash
# Désinstaller l'ancienne version
choco uninstall openjdk

# Installer Java 17
choco install openjdk17 -y
```

---

## Liens utiles

- **Adoptium (Temurin)** : https://adoptium.net/
- **Chocolatey** : https://chocolatey.org/
- **Maven** : https://maven.apache.org/

---

## Commandes rapides

```bash
# Vérifier Java
java -version

# Vérifier Maven
mvn -version

# Compiler un service
cd services\client-service
mvn clean install

# Démarrer un service
mvn spring-boot:run
```

---

## Prochaines étapes

Une fois Java et Maven installés :

1. ✅ Vérifier : `java -version` et `mvn -version`
2. ✅ Lancer : `start-services.bat`
3. ✅ Attendre 70 secondes
4. ✅ Tester : http://localhost:8761 (Eureka)
5. ✅ Tester : http://localhost:8080 (API Gateway)

---

**Temps d'installation : 5-10 minutes**

Besoin d'aide ? Consultez INSTALLATION.md pour plus de détails.
