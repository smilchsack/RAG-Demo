# Erste Stufe: Bauen des Spring Boot-Projekts mit Maven
FROM maven:3.9.5-eclipse-temurin-21-alpine AS builder

# Arbeitsverzeichnis im Container festlegen
WORKDIR /app

# Kopiere die pom.xml-Datei, um die Abhängigkeiten zu installieren
COPY pom.xml .

# Abhängigkeiten herunterladen (dies wird in einem separaten Schritt ausgeführt, um Caching zu ermöglichen)
RUN mvn dependency:go-offline -B

# Kopiere den Projektquellcode und führe den Build durch
COPY src ./src
RUN mvn clean package -DskipTests

# Zweite Stufe: Erstellen des finalen Docker Images mit dem gebauten Jar
FROM eclipse-temurin:21-jammy

# Arbeitsverzeichnis im Container festlegen
WORKDIR /app

# Kopiere das gebaute Jar aus der vorherigen Stufe
COPY --from=builder /app/target/*.jar app.jar

# Port, auf dem die Anwendung lauscht, öffnen
ENV PORT=8080
EXPOSE $PORT

# Befehl, um die Spring Boot-Anwendung beim Start des Containers auszuführen
CMD ["java", "-jar", "app.jar"]
