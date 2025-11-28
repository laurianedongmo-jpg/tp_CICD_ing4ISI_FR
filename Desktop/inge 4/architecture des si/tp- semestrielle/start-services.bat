@echo off
echo ========================================
echo WillBank Microservices - Demarrage
echo ========================================
echo.

REM Verifier Java
echo [1/4] Verification de Java...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERREUR: Java n'est pas installe ou pas dans le PATH
    echo Telechargez Java 17+ depuis: https://adoptium.net/
    pause
    exit /b 1
)
echo Java OK

REM Verifier Maven
echo [2/4] Verification de Maven...
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERREUR: Maven n'est pas installe ou pas dans le PATH
    echo Telechargez Maven depuis: https://maven.apache.org/download.cgi
    pause
    exit /b 1
)
echo Maven OK

echo.
echo [3/4] Compilation des services...
echo.

REM Compiler Discovery Service
echo Compilation du Discovery Service...
cd services\discovery-service
call mvn clean install -DskipTests
if %errorlevel% neq 0 (
    echo ERREUR lors de la compilation du Discovery Service
    cd ..\..
    pause
    exit /b 1
)
cd ..\..

REM Compiler Client Service
echo Compilation du Client Service...
cd services\client-service
call mvn clean install -DskipTests
if %errorlevel% neq 0 (
    echo ERREUR lors de la compilation du Client Service
    cd ..\..
    pause
    exit /b 1
)
cd ..\..

REM Compiler Compte Service
echo Compilation du Compte Service...
cd services\compte-service
call mvn clean install -DskipTests
if %errorlevel% neq 0 (
    echo ERREUR lors de la compilation du Compte Service
    cd ..\..
    pause
    exit /b 1
)
cd ..\..

REM Compiler Transaction Service
echo Compilation du Transaction Service...
cd services\transaction-service
call mvn clean install -DskipTests
if %errorlevel% neq 0 (
    echo ERREUR lors de la compilation du Transaction Service
    cd ..\..
    pause
    exit /b 1
)
cd ..\..

REM Compiler Notification Service
echo Compilation du Notification Service...
cd services\notification-service
call mvn clean install -DskipTests
if %errorlevel% neq 0 (
    echo ERREUR lors de la compilation du Notification Service
    cd ..\..
    pause
    exit /b 1
)
cd ..\..

REM Compiler Composite Service
echo Compilation du Composite Service...
cd services\composite-service
call mvn clean install -DskipTests
if %errorlevel% neq 0 (
    echo ERREUR lors de la compilation du Composite Service
    cd ..\..
    pause
    exit /b 1
)
cd ..\..

REM Compiler API Gateway
echo Compilation de l'API Gateway...
cd services\api-gateway
call mvn clean install -DskipTests
if %errorlevel% neq 0 (
    echo ERREUR lors de la compilation de l'API Gateway
    cd ..\..
    pause
    exit /b 1
)
cd ..\..

echo.
echo [4/4] Demarrage des services...
echo.
echo IMPORTANT: Sept fenetres vont s'ouvrir
echo - Fenetre 1: Discovery Service (Eureka) - Port 8761
echo - Fenetre 2: Client Service - Port 8081
echo - Fenetre 3: Compte Service - Port 8082
echo - Fenetre 4: Transaction Service - Port 8083
echo - Fenetre 5: Notification Service - Port 8084
echo - Fenetre 6: Composite Service - Port 8085
echo - Fenetre 7: API Gateway - Port 8080
echo.
echo Ne fermez pas ces fenetres !
echo.
pause

REM Demarrer Discovery Service dans une nouvelle fenetre
start "Discovery Service - Eureka" cmd /k "cd services\discovery-service && mvn spring-boot:run"

REM Attendre 15 secondes pour que Eureka demarre
echo Attente du demarrage d'Eureka (15 secondes)...
timeout /t 15 /nobreak

REM Demarrer Client Service dans une nouvelle fenetre
start "Client Service" cmd /k "cd services\client-service && mvn spring-boot:run"

REM Attendre 10 secondes
echo Attente du demarrage du Client Service (10 secondes)...
timeout /t 10 /nobreak

REM Demarrer Compte Service dans une nouvelle fenetre
start "Compte Service" cmd /k "cd services\compte-service && mvn spring-boot:run"

REM Attendre 10 secondes
echo Attente du demarrage du Compte Service (10 secondes)...
timeout /t 10 /nobreak

REM Demarrer Transaction Service dans une nouvelle fenetre
start "Transaction Service" cmd /k "cd services\transaction-service && mvn spring-boot:run"

REM Attendre 10 secondes
echo Attente du demarrage du Transaction Service (10 secondes)...
timeout /t 10 /nobreak

REM Demarrer Notification Service dans une nouvelle fenetre
start "Notification Service" cmd /k "cd services\notification-service && mvn spring-boot:run"

REM Attendre 10 secondes
echo Attente du demarrage du Notification Service (10 secondes)...
timeout /t 10 /nobreak

REM Demarrer Composite Service dans une nouvelle fenetre
start "Composite Service" cmd /k "cd services\composite-service && mvn spring-boot:run"

REM Attendre 10 secondes
echo Attente du demarrage du Composite Service (10 secondes)...
timeout /t 10 /nobreak

REM Demarrer API Gateway dans une nouvelle fenetre
start "API Gateway" cmd /k "cd services\api-gateway && mvn spring-boot:run"

echo.
echo ========================================
echo Services en cours de demarrage...
echo ========================================
echo.
echo API Gateway (Point d'entree): http://localhost:8080
echo Eureka Dashboard: http://localhost:8761
echo.
echo Services (via Gateway):
echo - Client: http://localhost:8080/api/clients
echo - Compte: http://localhost:8080/api/comptes
echo - Transaction: http://localhost:8080/api/transactions
echo - Notification: http://localhost:8080/api/notifications
echo - Composite: http://localhost:8080/api/composite
echo.
echo Services (direct):
echo - Client: http://localhost:8081
echo - Compte: http://localhost:8082
echo - Transaction: http://localhost:8083
echo - Notification: http://localhost:8084
echo - Composite: http://localhost:8085
echo.
echo H2 Consoles:
echo - Client: http://localhost:8081/h2-console
echo - Compte: http://localhost:8082/h2-console
echo - Transaction: http://localhost:8083/h2-console
echo - Notification: http://localhost:8084/h2-console
echo.
echo Attendez 70 secondes que tous les services demarrent
echo.
echo Tests disponibles:
echo - test-api-gateway.bat (NOUVEAU - Tester l'API Gateway)
echo - test-client-service.bat
echo - test-compte-service.bat
echo - test-transaction-service.bat
echo - test-composite-service.bat
echo - test-integration.bat
echo.
echo Pour arreter les services, fermez les fenetres ouvertes
echo.
pause
