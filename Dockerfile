FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the jar that you built locally
COPY target/*.jar app.jar

EXPOSE 8080
ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
