FROM eclipse-temurin:21-jre-alpine

LABEL maintainer="tp-cicd-app"
LABEL description="Spring Boot Application with MySQL"

# Créer un utilisateur non-root pour la sécurité
RUN addgroup -S spring && adduser -S spring -G spring

# Définir le répertoire de travail
WORKDIR /app

# Copier le JAR de l'application
COPY target/*.jar app.jar

# Changer le propriétaire du fichier
RUN chown spring:spring app.jar

# Basculer vers l'utilisateur non-root
USER spring:spring

# Exposer le port de l'application
EXPOSE 8080

# Variables d'environnement par défaut (seront remplacées au runtime)
ENV JAVA_OPTS="-Xms256m -Xmx512m" \
    SERVER_PORT=8080

# Point d'entrée avec gestion des variables d'environnement
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app/app.jar"]

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1
