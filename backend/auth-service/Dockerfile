# ===== Stage 1: BUILD the JAR (this image has Maven and the full JDK) =====
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app

# Copy pom.xml on its own first and download the dependencies.
# Docker caches every step ("layer"): while pom.xml is unchanged, this slow step is skipped next time.
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# Then the source code, and package it (the tests already ran on your machine)
COPY src ./src
RUN mvn -B -q package -DskipTests

# ===== Stage 2: RUN the JAR (only the small Java runtime, no Maven, no source code) =====
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8082

# -Duser.timezone: containers default to UTC. Dates are stored in MongoDB as midnight India time,
# so the JVM must use the same zone to read them back as the same day.
ENTRYPOINT ["java", "-Duser.timezone=Asia/Kolkata", "-jar", "app.jar"]
