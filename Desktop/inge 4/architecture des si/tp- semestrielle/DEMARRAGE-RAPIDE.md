# 🚀 Démarrage Rapide - 5 minutes

## Étape 1 : Vérifier les prérequis (1 minute)

Ouvrir un terminal (PowerShell ou CMD) et taper :

```bash
java -version
```
✅ Doit afficher : Java 17 ou supérieur

```bash
mvn -version
```
✅ Doit afficher : Maven 3.8 ou supérieur

❌ **Si une commande ne fonctionne pas :**
→ Lire le fichier **INSTALLATION.md** pour installer les prérequis

## Étape 2 : Démarrer les services (2 minutes)

### Option A : Automatique (Recommandé)
Double-cliquer sur le fichier : **`start-services.bat`**

Deux fenêtres vont s'ouvrir :
1. Discovery Service (Eureka)
2. Client Service

⏳ Attendre 30 secondes que les services démarrent

### Option B : Manuel

**Terminal 1 :**
```bash
cd services\discovery-service
mvn spring-boot:run
```

**Terminal 2 :** (attendre 15 secondes puis)
```bash
cd services\client-service
mvn spring-boot:run
```

## Étape 3 : Vérifier que ça marche (1 minute)

### Dans le navigateur

**Eureka Dashboard :**
```
http://localhost:8761
```
✅ Vous devez voir "CLIENT-SERVICE" dans la liste

**H2 Console (Base de données) :**
```
http://localhost:8081/h2-console
```
- JDBC URL : `jdbc:h2:mem:willbank_clients`
- Username : `sa`
- Password : (laisser vide)
- Cliquer "Connect"

## Étape 4 : Tester l'API (1 minute)

### Option A : Script automatique
Double-cliquer sur : **`test-client-service.bat`**

### Option B : Manuel avec cURL

**Créer un client :**
```bash
curl -X POST http://localhost:8081/api/clients -H "Content-Type: application/json" -d "{\"nom\":\"DIALLO\",\"prenom\":\"Mamadou\",\"dateNaissance\":\"1990-05-15\",\"adresse\":\"Dakar, Senegal\",\"telephone\":\"+221771234567\",\"email\":\"mamadou.diallo@example.com\",\"typeClient\":\"PARTICULIER\"}"
```

**Lister les clients :**
```bash
curl http://localhost:8081/api/clients
```

**Obtenir un client :**
```bash
curl http://localhost:8081/api/clients/1
```

## ✅ C'est tout !

Vous avez maintenant :
- ✅ Discovery Service (Eureka) qui tourne
- ✅ Client Service qui tourne
- ✅ Une API REST fonctionnelle
- ✅ Une base de données H2

## 🎯 Prochaines étapes

1. **Tester toutes les APIs** → Voir TESTING-GUIDE.md
2. **Implémenter les autres services** → Voir docs/guide-implementation.md
3. **Ajouter Docker** → Voir infrastructure/docker-compose.yml

## 🆘 Problèmes ?

### Le service ne démarre pas
1. Vérifier que Java 17+ est installé : `java -version`
2. Vérifier que Maven est installé : `mvn -version`
3. Vérifier les logs dans le terminal

### Port déjà utilisé
Un autre service utilise le port 8081 ou 8761.
→ Arrêter l'autre service ou changer le port dans `application.yml`

### CLIENT-SERVICE n'apparaît pas dans Eureka
→ Attendre 30 secondes (délai d'enregistrement)

### Erreur "Cannot connect to database"
→ Vérifier la configuration H2 dans `application.yml`

## 📚 Documentation complète

- **Installation détaillée :** INSTALLATION.md
- **Guide de test :** TESTING-GUIDE.md
- **Architecture :** docs/architecture-globale.md
- **État du projet :** STATUS.md

---

**Temps total : ~5 minutes** ⏱️
