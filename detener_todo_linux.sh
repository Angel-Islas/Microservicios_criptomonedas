#!/bin/bash

PID_FILE="cryptotracker.pids"

if [[ -f "$PID_FILE" ]]; then
    echo "Deteniendo servicios de CryptoTracker..."
    while read -r pid; do
        if kill -0 "$pid" 2>/dev/null; then
            kill "$pid"
            echo "Proceso $pid detenido."
        else
            echo "Proceso $pid no existe o ya fue detenido."
        fi
    done < "$PID_FILE"
    rm "$PID_FILE"
    echo "Todos los servicios han sido detenidos."
else
    echo "No se encontró el archivo de PIDs. ¿Los servicios están corriendo?"
fi
