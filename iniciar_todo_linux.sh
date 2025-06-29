#!/bin/bash

echo "Iniciando servicios de CryptoTracker..."

BASE=$(pwd)
PID_FILE="$BASE/cryptotracker.pids"
> "$PID_FILE"

# ============================
cd "$BASE/DataCollectorService" || exit
echo "Compilando DataCollectorService..."
javac -encoding UTF-8 -cp ".:../shared/json.jar" *.java
java -cp .:../shared/json.jar Main & echo $! >> "$PID_FILE"

# ============================
cd "$BASE/CryptoPriceAPI" || exit
echo "Compilando CryptoPriceAPI..."
javac -encoding UTF-8 -cp ".:../shared/json.jar" *.java
java -cp .:../shared/json.jar WebServer & echo $! >> "$PID_FILE"

# ============================
cd "$BASE/GraphService" || exit
echo "Compilando GraphService..."
javac -encoding UTF-8 -cp ".:../shared/json.jar:../shared/jfreechart.jar:../shared/jcommon.jar" *.java
java -cp .:../shared/json.jar:../shared/jfreechart.jar:../shared/jcommon.jar GraphServer & echo $! >> "$PID_FILE"

# ============================
cd "$BASE/RegressionService" || exit
echo "Compilando RegressionService..."
javac -encoding UTF-8 -cp ".:../shared/json.jar:../shared/jfreechart.jar:../shared/jcommon.jar" *.java
java -cp .:../shared/json.jar:../shared/jfreechart.jar:../shared/jcommon.jar RegressionServer & echo $! >> "$PID_FILE"

cd "$BASE"
echo "Todos los servicios están en ejecución."
