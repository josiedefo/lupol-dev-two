# ========= 1) BUILD STAGE =========
FROM maven:3.9.9-eclipse-temurin-21 AS build

# Set working dir inside the container
WORKDIR /app

# Copy pom and source
COPY pom.xml .
COPY src ./src
COPY frontend ./frontend

# (Optional but recommended) Pre-download Maven deps
RUN mvn -q -e -DskipTests dependency:go-offline

# Build the app (this will also build the Vue frontend via your plugins)
RUN mvn -q -e -DskipTests clean package

# ========= 2) RUNTIME STAGE =========
FROM eclipse-temurin:21-jre

# Set working dir
WORKDIR /app

# Copy the built jar from the build stage
# Adjust the jar name if your artifactId/version differ
COPY --from=build /app/target/*.jar app.jar

# Expose the Spring Boot port (default 8080)
EXPOSE 8080

# JVM options (optional)
ENV JAVA_OPTS=""

# Context path already configured in application.properties as /lupoldevtwo
# If you want to override, you can pass: -Dserver.servlet.context-path=/something
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
