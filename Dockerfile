# Use official Maven (with JDK 17) to build the project
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy the project files
COPY pom.xml .
COPY src ./src
COPY mvnw .
COPY .mvn ./.mvn

# Build the JAR
RUN mvn -q -e -DskipTests clean package

# --- Runtime image ---
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copy built jar from previous step
COPY --from=build /app/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
