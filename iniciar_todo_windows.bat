@echo off
setlocal

echo Iniciando servicios de CryptoTracker...

:: Ruta base
set BASE=%cd%

:: ============================
:: Iniciar DataCollectorService
cd /d "%BASE%\DataCollectorService"
echo Compilando DataCollectorService...
javac -cp ".;..\shared\json.jar" *.java
start cmd /k java -cp ".;..\shared\json.jar" Main

:: ============================
:: Iniciar CryptoPriceAPI
cd /d "%BASE%\CryptoPriceAPI"
echo Compilando CryptoPriceAPI...
javac -cp ".;..\shared\json.jar" *.java
start cmd /k java -cp ".;..\shared\json.jar" WebServer

:: ============================
:: Iniciar GraphService
cd /d "%BASE%\GraphService"
echo Compilando GraphService...
javac -cp ".;..\shared\json.jar;..\shared\jfreechart.jar;..\shared\jcommon.jar" *.java
start cmd /k java -cp ".;..\shared\json.jar;..\shared\jfreechart.jar;..\shared\jcommon.jar" GraphServer

:: ============================
:: Iniciar RegressionService
cd /d "%BASE%\RegressionService"
echo Compilando RegressionService...
javac -cp ".;..\shared\json.jar;..\shared\jfreechart.jar;..\shared\jcommon.jar" *.java
start cmd /k java -cp ".;..\shared\json.jar;..\shared\jfreechart.jar;..\shared\jcommon.jar" RegressionServer

:: ============================
cd /d "%BASE%"
echo Todos los servicios han sido iniciados en nuevas ventanas.
pause
