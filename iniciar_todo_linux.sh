#!/bin/bash

echo "Iniciando servicios de CryptoTracker..."

# Ruta base
BASE=$(pwd)

# ============================
# Iniciar DataCollectorService
cd "$BASE/DataCollectorService"
echo "Compilando DataCollectorService..."
javac -cp ".:../shared/json.jar" *.java
gnome-terminal -- bash -c "java -cp .:../shared/json.jar Main; exec bash"

# ============================
# Iniciar CryptoPriceAPI
cd "$BASE/CryptoPriceAPI"
echo "Compilando CryptoPriceAPI..."
javac -cp ".:../shared/json.jar" *.java
gnome-terminal -- bash -c "java -cp .:../shared/json.jar WebServer; exec bash"

# ============================
# Iniciar GraphService
cd "$BASE/GraphService"
echo "Compilando GraphService..."
javac -cp ".:../shared/json.jar:../shared/jfreechart.jar:../shared/jcommon.jar" *.java
gnome-terminal -- bash -c "java -cp .:../shared/json.jar:../shared/jfreechart.jar:../shared/jcommon.jar GraphServer; exec bash"

# ============================
# Iniciar RegressionService
cd "$BASE/RegressionService"
echo "Compilando RegressionService..."
javac -cp ".:../shared/json.jar:../shared/jfreechart.jar:../shared/jcommon.jar" *.java
gnome-terminal -- bash -c "java -cp .:../shared/json.jar:../shared/jfreechart.jar:../shared/jcommon.jar RegressionServer; exec bash"

cd "$BASE"
echo "Todos los servicios están en ejecución."
