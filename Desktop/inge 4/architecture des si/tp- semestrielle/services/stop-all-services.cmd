@echo off
echo === Arret des services WillBank ===
taskkill /F /IM java.exe /T 2>nul
if %ERRORLEVEL% EQU 0 (
    echo Tous les services ont ete arretes.
) else (
    echo Aucun processus Java en cours.
)
pause
