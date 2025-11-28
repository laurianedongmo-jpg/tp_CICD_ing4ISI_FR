@echo off
echo === Verification des services ===
echo.
echo Discovery Service (8761):
netstat -ano | findstr :8761
echo.
echo Client Service (8081):
netstat -ano | findstr :8081
echo.
echo Compte Service (8082):
netstat -ano | findstr :8082
echo.
echo Transaction Service (8083):
netstat -ano | findstr :8083
echo.
echo Composite Service (8085):
netstat -ano | findstr :8085
echo.
echo API Gateway (8080):
netstat -ano | findstr :8080
echo.
pause
