@echo off
echo ========================================
echo Test du Client Service
echo ========================================
echo.

set BASE_URL=http://localhost:8081/api/clients

echo Test 1: Creer un client
echo.
curl -X POST %BASE_URL% ^
  -H "Content-Type: application/json" ^
  -d "{\"nom\":\"DIALLO\",\"prenom\":\"Mamadou\",\"dateNaissance\":\"1990-05-15\",\"adresse\":\"Dakar, Senegal\",\"telephone\":\"+221771234567\",\"email\":\"mamadou.diallo@example.com\",\"typeClient\":\"PARTICULIER\"}"

echo.
echo.
echo ========================================
timeout /t 2 /nobreak >nul

echo Test 2: Lister tous les clients
echo.
curl %BASE_URL%

echo.
echo.
echo ========================================
timeout /t 2 /nobreak >nul

echo Test 3: Obtenir le client ID 1
echo.
curl %BASE_URL%/1

echo.
echo.
echo ========================================
timeout /t 2 /nobreak >nul

echo Test 4: Valider le KYC du client 1
echo.
curl -X POST %BASE_URL%/1/kyc/valider

echo.
echo.
echo ========================================
timeout /t 2 /nobreak >nul

echo Test 5: Creer un deuxieme client
echo.
curl -X POST %BASE_URL% ^
  -H "Content-Type: application/json" ^
  -d "{\"nom\":\"NDIAYE\",\"prenom\":\"Fatou\",\"dateNaissance\":\"1995-08-20\",\"adresse\":\"Thies, Senegal\",\"telephone\":\"+221779876543\",\"email\":\"fatou.ndiaye@example.com\",\"typeClient\":\"PARTICULIER\"}"

echo.
echo.
echo ========================================
timeout /t 2 /nobreak >nul

echo Test 6: Rechercher des clients
echo.
curl "%BASE_URL%/search?term=DIALLO"

echo.
echo.
echo ========================================
echo Tests termines !
echo.
pause
