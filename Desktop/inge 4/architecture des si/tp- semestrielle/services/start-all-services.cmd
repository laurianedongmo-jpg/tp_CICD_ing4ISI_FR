@echo off
echo === Demarrage des services WillBank ===

echo.
echo 1. Demarrage de Discovery Service (Eureka)...
start "Discovery Service" cmd /k "cd /d ..\discovery-service && mvn spring-boot:run"
timeout /t 40 /nobreak

echo.
echo 2. Demarrage des services metier...
start "Client Service" cmd /k "cd /d ..\client-service && mvn spring-boot:run"
start "Compte Service" cmd /k "cd /d ..\compte-service && mvn spring-boot:run"
start "Transaction Service" cmd /k "cd /d ..\transaction-service && mvn spring-boot:run"
timeout /t 25 /nobreak

echo.
echo 3. Demarrage du Composite Service...
start "Composite Service" cmd /k "cd /d ..\composite-service && mvn spring-boot:run"
timeout /t 10 /nobreak

echo.
echo 4. Demarrage de l API Gateway...
start "API Gateway" cmd /k "cd /d ..\api-gateway && mvn spring-boot:run"

echo.
echo === Tous les services sont en cours de demarrage ===
echo Verifiez Eureka: http://localhost:8761
pause
