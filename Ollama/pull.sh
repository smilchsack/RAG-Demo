#!/bin/bash

# Starte den Dienst im Hintergrund.
printf "Starte Ollama Service \n"
/bin/ollama serve &

# Warte kurz, damit der Dienst gestartet ist.
sleep 5

# Das Modell lokal herunterladen.
/bin/ollama pull mistral