@echo off
echo ========================================
echo   Demarrage des services WillBank
echo ========================================
echo.
echo IMPORTANT: Assurez-vous que Maven est installe
echo ou utilisez IntelliJ IDEA pour demarrer les services
echo.
echo Pour demarrer avec IntelliJ:
echo 1. Ouvrez chaque projet dans IntelliJ
echo 2. Trouvez la classe principale (*Application.java)
echo 3. Clic droit sur la classe -^> Run
echo.
echo Ordre de demarrage:
echo   1. discovery-service (port 8761)
echo   2. client-service (port 8081)
echo   3. compte-service (port 8082)
echo   4. transaction-service (port 8083)
echo   5. composite-service (port 8085)
echo   6. api-gateway (port 8080)
echo.
echo Verifiez Eureka: http://localhost:8761
echo.
pause
