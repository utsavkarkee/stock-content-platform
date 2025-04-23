# Use official Maven image to build the application
FROM maven:3.8.3-openjdk-17 AS build
WORKDIR /app

# Copy the project files
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Use a minimal JDK image to run the application
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy the built JAR from the previous stage
COPY --from=build /app/target/auth-service-*.jar auth-service.jar

# Run the application
ENTRYPOINT ["java", "-jar", "auth-service.jar"]
