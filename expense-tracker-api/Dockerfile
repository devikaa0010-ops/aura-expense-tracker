# Stage 1: Build the application using Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# We skip tests here to dramatically speed up the cloud deployment spin-up time
RUN mvn clean package -DskipTests

# Stage 2: Run the application using a lightweight JRE
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Expose the standard port (overridden natively by Render if needed)
ENV PORT=8080
EXPOSE ${PORT}

# Run the jar
ENTRYPOINT ["java", "-Dserver.port=${PORT}", "-jar", "app.jar"]
