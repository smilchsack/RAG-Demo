#!/bin/bash

# Starte den Dienst im Hintergrund.
printf "Starte Ollama Service \n"
/bin/ollama serve &

# Warte kurz, damit der Dienst gestartet ist.
sleep 5

# Das Modell in einer zweiten Konsole starten.
printf "Starte Mistral\n"
/bin/ollama run mistral &