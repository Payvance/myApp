# Spring Boot Core SaaS container
FROM eclipse-temurin:17

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Make Maven wrapper executable
RUN chmod +x ./mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# Expose port 8080 (default Spring Boot port)
EXPOSE 8080

# Run the Spring Boot application
CMD ["java", "-jar", "target/erp-saas-0.0.1-SNAPSHOT.jar"]
