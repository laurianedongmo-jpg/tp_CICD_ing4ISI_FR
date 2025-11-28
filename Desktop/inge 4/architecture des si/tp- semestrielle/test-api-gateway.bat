@echo off
echo ========================================
echo Test de l'API Gateway
echo ========================================
echo.

set GATEWAY_URL=http://localhost:8080

echo Test 1: Health check
echo.
curl %GATEWAY_URL%/api/auth/health

echo.
echo.
timeout /t 2 /nobreak >nul

echo Test 2: Login avec admin
echo.
curl -X POST %GATEWAY_URL%/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"admin\",\"password\":\"admin123\"}"

echo.
echo.
echo Copiez le token ci-dessus pour les tests suivants
echo.
pause

REM Remplacer YOUR_TOKEN par le token obtenu
set TOKEN=YOUR_TOKEN

echo.
echo Test 3: Valider le token
echo.
curl %GATEWAY_URL%/api/auth/validate ^
  -H "Authorization: Bearer %TOKEN%"

echo.
echo.
timeout /t 2 /nobreak >nul

echo Test 4: Acceder au Client Service via Gateway (avec token)
echo.
curl %GATEWAY_URL%/api/clients ^
  -H "Authorization: Bearer %TOKEN%"

echo.
echo.
timeout /t 2 /nobreak >nul

echo Test 5: Acceder au Composite Service via Gateway (avec token)
echo.
curl %GATEWAY_URL%/api/composite/dashboard/1 ^
  -H "Authorization: Bearer %TOKEN%"

echo.
echo.
timeout /t 2 /nobreak >nul

echo Test 6: Tenter d'acceder sans token (doit echouer)
echo.
curl %GATEWAY_URL%/api/clients

echo.
echo.
timeout /t 2 /nobreak >nul

echo Test 7: Login avec user
echo.
curl -X POST %GATEWAY_URL%/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"user\",\"password\":\"user123\"}"

echo.
echo.
echo ========================================
echo Tests termines !
echo.
echo Comptes de test:
echo - admin / admin123 (ROLE_ADMIN, ROLE_USER)
echo - user / user123 (ROLE_USER)
echo.
echo L'API Gateway route toutes les requetes vers les microservices
echo et gere l'authentification JWT.
echo.
pause
