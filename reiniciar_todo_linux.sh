#!/bin/bash

echo "Reiniciando servicios de CryptoTracker..."
./detener_todo_linux.sh
sleep 2
./iniciar_todo_linux.sh
