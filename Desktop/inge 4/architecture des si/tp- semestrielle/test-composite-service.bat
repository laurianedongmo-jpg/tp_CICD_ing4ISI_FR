@echo off
echo ========================================
echo Test du Composite Service
echo ========================================
echo.
echo PREREQUIS: Client, Compte et Transaction doivent avoir des donnees
echo.

set COMPOSITE_URL=http://localhost:8085/api/composite

echo Preparation: Creer des donnees de test
echo.

REM Creer un client et des comptes avec des transactions
curl -X POST http://localhost:8081/api/clients -H "Content-Type: application/json" -d "{\"nom\":\"COMPOSITE\",\"prenom\":\"Test\",\"dateNaissance\":\"1990-01-01\",\"adresse\":\"Test\",\"telephone\":\"+221770000000\",\"email\":\"composite@test.com\",\"typeClient\":\"PARTICULIER\"}" >nul 2>&1
timeout /t 1 /nobreak >nul

curl -X POST http://localhost:8081/api/clients/1/kyc/valider >nul 2>&1
timeout /t 1 /nobreak >nul

curl -X POST http://localhost:8082/api/comptes -H "Content-Type: application/json" -d "{\"clientId\":1,\"typeCompte\":\"COURANT\",\"devise\":\"XOF\",\"decouvertAutorise\":50000}" >nul 2>&1
timeout /t 1 /nobreak >nul

curl -X POST http://localhost:8082/api/comptes -H "Content-Type: application/json" -d "{\"clientId\":1,\"typeCompte\":\"EPARGNE\",\"devise\":\"XOF\"}" >nul 2>&1
timeout /t 1 /nobreak >nul

curl -X PATCH http://localhost:8082/api/comptes/1/solde -H "Content-Type: application/json" -d "{\"montant\":500000,\"operation\":\"CREDIT\",\"version\":0}" >nul 2>&1
timeout /t 1 /nobreak >nul

curl -X POST http://localhost:8083/api/transactions/virement -H "Content-Type: application/json" -d "{\"compteSourceId\":1,\"compteDestinationId\":2,\"montant\":100000,\"devise\":\"XOF\",\"description\":\"Test\"}" >nul 2>&1
timeout /t 1 /nobreak >nul

echo Preparation terminee !
echo.
echo ========================================
echo.

echo Test 1: Dashboard complet du client 1
echo.
curl %COMPOSITE_URL%/dashboard/1

echo.
echo.
timeout /t 2 /nobreak >nul

echo Test 2: Vue d'ensemble des comptes du client 1
echo.
curl %COMPOSITE_URL%/comptes/1/overview

echo.
echo.
timeout /t 2 /nobreak >nul

echo Test 3: Releve du compte 1 (dernier mois)
echo.
set DATE_DEBUT=2025-11-01T00:00:00
set DATE_FIN=2025-11-30T23:59:59
curl "%COMPOSITE_URL%/releve/1?dateDebut=%DATE_DEBUT%&dateFin=%DATE_FIN%"

echo.
echo.
timeout /t 2 /nobreak >nul

echo Test 4: Health check
echo.
curl %COMPOSITE_URL%/health

echo.
echo.
echo ========================================
echo Tests termines !
echo.
echo Le dashboard devrait afficher:
echo - Informations du client
echo - Liste des comptes avec soldes
echo - Dernieres transactions
echo - Statistiques (nombre de comptes, solde total, etc.)
echo.
pause
