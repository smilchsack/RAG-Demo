# RAG Beispiel - Rede mit deinen Dokumenten

Es handelt sich um eine einfache ChatApp, mit der Sie Dokumente befragen können.
Die Anwendung kennt ein Paper zu Milvus und HNSW, sowie zwei Webseiten zu HNSW.
Als lokales Modell wird Mistral mithilfe von Ollama eingesetzt.
Zum Speichern der Dokumente kommt Milvus zum Einsatz. Das Backend basiert auf Spring Boot
und integriert die Vektordatenbank und das lokale LLM mittels LangChain4J.
Zur grafischen Darstellung greift eine auf Angular basierende SPA-Applikation
auf das Backend zu.

## Authors

- [Sebastian Milchsack](mailto:milchsack.sebastian@fh-swf.de)

## Hardware Anforderungen

Es wird empfohlen mindestens, 15 GB freien Speicherplatz zur Verfügung zu haben
sowie mindestens 4 CPU Kerne und 8 GB RAM.

## Starten der Demo-Umgebung

Die Umgebung verwendet die folgenden Ports:

- Milvus: 19530, 9091, 9000 & 9000
- Ollama: 11434
- Spring Boot: 8080 (im dev-Profile 8181)
- Angular: 4000 (außerhalb von Docker 4200)

Die genannten Ports müssen unbelegt sein oder in der Docker-Compose-Datei angepasst werden.
Außerdem wird eine Internetverbindung benötigt.

Zum Starten der Anwendung wird nur Docker-Desktop benötigt. Verwendet wurde die Version
4.29.0 (145265).

Hierzu sollte in einem Terminal (z.B. PowerShell) in das Hauptverzeichnis der Demo-Anwendung gewechselt werden.
In diesem Verzeichnis befindet sich diese README- sowie die Docker-Compose-Datei.
Anschließend wird die Anwendung mit dem nachfolgenden Befehl gestartet.

````
docker-compose up -d 
````

Der Prozess kann einige Minuten in Anspruch nehmen. Dies liegt unter anderem
daran, dass das LLM Mistral ca. 4 GB groß ist und zuerst heruntergeladen werden muss.

Sobald alle Dienste gestartet sind, 
kann die Demo-Anwendung über die nachfolgende URL geöffnet werden: http://localhost:4000

## Beenden der Demo-Umgebung

Zum Beenden und vollständigen Entfernen der Demo-Anwendung siehe "Deinstallieren der Demo-Umgebung".

Zum Beenden der Anwendung muss in das Hauptverzeichnis in einem Terminal gewechselt werden.

Anschließend wird die Anwendung mit dem nachfolgenden Befehl beendet.

````
docker-compose down
````

## Deinstallieren der Demo-Umgebung

Um die Anwendung vollständig zu entfernen, sollten die folgenden Befehle ausgeführt werden:

````
docker-compose down --volumes --rmi all
docker image prune
docker volume prune
rm -rf volumes
````
