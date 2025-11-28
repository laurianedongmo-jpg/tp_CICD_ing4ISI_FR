@echo off
echo ========================================
echo Verification de l'installation
echo ========================================
echo.

set ALL_OK=1

echo [1/2] Verification de Java...
java -version >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] Java est installe
    java -version
) else (
    echo [ERREUR] Java n'est pas installe ou pas dans le PATH
    echo.
    echo Solutions:
    echo 1. Installer avec Chocolatey: choco install openjdk17 -y
    echo 2. Telecharger depuis: https://adoptium.net/
    echo 3. Voir le guide: GUIDE-INSTALLATION-JDK.md
    set ALL_OK=0
)

echo.
echo ========================================
echo.

echo [2/2] Verification de Maven...
mvn -version >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] Maven est installe
    mvn -version
) else (
    echo [ERREUR] Maven n'est pas installe ou pas dans le PATH
    echo.
    echo Solutions:
    echo 1. Installer avec Chocolatey: choco install maven -y
    echo 2. Telecharger depuis: https://maven.apache.org/download.cgi
    echo 3. Voir le guide: GUIDE-INSTALLATION-JDK.md
    set ALL_OK=0
)

echo.
echo ========================================
echo.

if %ALL_OK% equ 1 (
    echo [SUCCESS] Tous les prerequis sont installes !
    echo.
    echo Vous pouvez maintenant demarrer les services:
    echo   start-services.bat
    echo.
) else (
    echo [ATTENTION] Certains prerequis manquent
    echo.
    echo Veuillez installer les outils manquants puis relancer ce script.
    echo Consultez: GUIDE-INSTALLATION-JDK.md
    echo.
)

pause
