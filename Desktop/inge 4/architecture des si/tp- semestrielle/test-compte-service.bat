@echo off
echo ========================================
echo Test du Compte Service
echo ========================================
echo.

set BASE_URL=http://localhost:8082/api/comptes

echo Test 1: Creer un compte courant pour le client 1
echo.
curl -X POST %BASE_URL% ^
  -H "Content-Type: application/json" ^
  -d "{\"clientId\":1,\"typeCompte\":\"COURANT\",\"devise\":\"XOF\",\"decouvertAutorise\":50000}"

echo.
echo.
echo ========================================
timeout /t 2 /nobreak >nul

echo Test 2: Creer un compte epargne pour le client 1
echo.
curl -X POST %BASE_URL% ^
  -H "Content-Type: application/json" ^
  -d "{\"clientId\":1,\"typeCompte\":\"EPARGNE\",\"devise\":\"XOF\",\"tauxInteret\":2.5}"

echo.
echo.
echo ========================================
timeout /t 2 /nobreak >nul

echo Test 3: Lister les comptes du client 1
echo.
curl %BASE_URL%/client/1

echo.
echo.
echo ========================================
timeout /t 2 /nobreak >nul

echo Test 4: Crediter le compte 1 de 100000 XOF
echo.
curl -X PATCH %BASE_URL%/1/solde ^
  -H "Content-Type: application/json" ^
  -d "{\"montant\":100000,\"operation\":\"CREDIT\",\"version\":0}"

echo.
echo.
echo ========================================
timeout /t 2 /nobreak >nul

echo Test 5: Debiter le compte 1 de 25000 XOF
echo.
curl -X PATCH %BASE_URL%/1/solde ^
  -H "Content-Type: application/json" ^
  -d "{\"montant\":25000,\"operation\":\"DEBIT\",\"version\":1}"

echo.
echo.
echo ========================================
timeout /t 2 /nobreak >nul

echo Test 6: Verifier le solde disponible
echo.
curl "%BASE_URL%/1/solde/disponible?montant=50000"

echo.
echo.
echo ========================================
timeout /t 2 /nobreak >nul

echo Test 7: Obtenir le compte 1
echo.
curl %BASE_URL%/1

echo.
echo.
echo ========================================
echo Tests termines !
echo.
pause
