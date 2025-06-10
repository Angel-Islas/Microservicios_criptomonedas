#!/bin/bash

echo "Iniciando servicios de CryptoTracker (macOS)..."

BASE=$(pwd)

# =============== Function ===============
run_in_terminal() {
  SCRIPT_PATH=$1
  osascript <<EOF
tell application "Terminal"
  do script "cd \"$SCRIPT_PATH\"; clear; ./run.sh"
end tell
EOF
}

# ============================
# DataCollectorService
mkdir -p "$BASE/DataCollectorService"
cat > "$BASE/DataCollectorService/run.sh" <<EORUN
#!/bin/bash
javac -cp ".:../shared/json.jar" *.java
java -cp ".:../shared/json.jar" Main
EORUN
chmod +x "$BASE/DataCollectorService/run.sh"
run_in_terminal "$BASE/DataCollectorService"

# ============================
# CryptoPriceAPI
mkdir -p "$BASE/CryptoPriceAPI"
cat > "$BASE/CryptoPriceAPI/run.sh" <<EORUN
#!/bin/bash
javac -cp ".:../shared/json.jar" *.java
java -cp ".:../shared/json.jar" WebServer
EORUN
chmod +x "$BASE/CryptoPriceAPI/run.sh"
run_in_terminal "$BASE/CryptoPriceAPI"

# ============================
# GraphService
mkdir -p "$BASE/GraphService"
cat > "$BASE/GraphService/run.sh" <<EORUN
#!/bin/bash
javac -cp ".:../shared/json.jar:../shared/jfreechart.jar:../shared/jcommon.jar" *.java
java -cp ".:../shared/json.jar:../shared/jfreechart.jar:../shared/jcommon.jar" GraphServer
EORUN
chmod +x "$BASE/GraphService/run.sh"
run_in_terminal "$BASE/GraphService"

# ============================
# RegressionService
mkdir -p "$BASE/RegressionService"
cat > "$BASE/RegressionService/run.sh" <<EORUN
#!/bin/bash
javac -cp ".:../shared/json.jar:../shared/jfreechart.jar:../shared/jcommon.jar" *.java
java -cp ".:../shared/json.jar:../shared/jfreechart.jar:../shared/jcommon.jar" RegressionServer
EORUN
chmod +x "$BASE/RegressionService/run.sh"
run_in_terminal "$BASE/RegressionService"

echo "Todos los servicios fueron lanzados en nuevas ventanas de Terminal."
