@echo off
echo ========================================
echo Test d'integration Client + Compte
echo ========================================
echo.

set CLIENT_URL=http://localhost:8081/api/clients
set COMPTE_URL=http://localhost:8082/api/comptes

echo Scenario: Creer un client puis ouvrir des comptes
echo.
echo ========================================
echo.

echo Etape 1: Creer un client
echo.
curl -X POST %CLIENT_URL% ^
  -H "Content-Type: application/json" ^
  -d "{\"nom\":\"NDIAYE\",\"prenom\":\"Fatou\",\"dateNaissance\":\"1995-08-20\",\"adresse\":\"Thies, Senegal\",\"telephone\":\"+221779876543\",\"email\":\"fatou.ndiaye@example.com\",\"typeClient\":\"PARTICULIER\"}"

echo.
echo.
timeout /t 2 /nobreak >nul

echo Etape 2: Valider le KYC du client
echo.
curl -X POST %CLIENT_URL%/1/kyc/valider

echo.
echo.
timeout /t 2 /nobreak >nul

echo Etape 3: Creer un compte courant pour le client
echo.
curl -X POST %COMPTE_URL% ^
  -H "Content-Type: application/json" ^
  -d "{\"clientId\":1,\"typeCompte\":\"COURANT\",\"devise\":\"XOF\",\"decouvertAutorise\":50000}"

echo.
echo.
timeout /t 2 /nobreak >nul

echo Etape 4: Creer un compte epargne pour le client
echo.
curl -X POST %COMPTE_URL% ^
  -H "Content-Type: application/json" ^
  -d "{\"clientId\":1,\"typeCompte\":\"EPARGNE\",\"devise\":\"XOF\",\"tauxInteret\":2.5}"

echo.
echo.
timeout /t 2 /nobreak >nul

echo Etape 5: Lister tous les comptes du client
echo.
curl %COMPTE_URL%/client/1

echo.
echo.
timeout /t 2 /nobreak >nul

echo Etape 6: Crediter le compte courant de 500000 XOF
echo.
curl -X PATCH %COMPTE_URL%/1/solde ^
  -H "Content-Type: application/json" ^
  -d "{\"montant\":500000,\"operation\":\"CREDIT\",\"version\":0}"

echo.
echo.
timeout /t 2 /nobreak >nul

echo Etape 7: Crediter le compte epargne de 1000000 XOF
echo.
curl -X PATCH %COMPTE_URL%/2/solde ^
  -H "Content-Type: application/json" ^
  -d "{\"montant\":1000000,\"operation\":\"CREDIT\",\"version\":0}"

echo.
echo.
timeout /t 2 /nobreak >nul

echo Etape 8: Effectuer un retrait de 50000 XOF du compte courant
echo.
curl -X PATCH %COMPTE_URL%/1/solde ^
  -H "Content-Type: application/json" ^
  -d "{\"montant\":50000,\"operation\":\"DEBIT\",\"version\":1}"

echo.
echo.
timeout /t 2 /nobreak >nul

echo Etape 9: Verifier le solde disponible pour un retrait de 100000 XOF
echo.
curl "%COMPTE_URL%/1/solde/disponible?montant=100000"

echo.
echo.
timeout /t 2 /nobreak >nul

echo Etape 10: Afficher les informations du client
echo.
curl %CLIENT_URL%/1

echo.
echo.
timeout /t 2 /nobreak >nul

echo Etape 11: Afficher tous les comptes du client
echo.
curl %COMPTE_URL%/client/1

echo.
echo.
echo ========================================
echo Test d'integration termine !
echo.
echo Resultat attendu:
echo - 1 client cree et KYC valide
echo - 2 comptes crees (courant et epargne)
echo - Compte courant: solde = 450000 XOF
echo - Compte epargne: solde = 1000000 XOF
echo.
pause
