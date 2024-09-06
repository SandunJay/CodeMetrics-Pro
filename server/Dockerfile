# Use an official Java runtime as a parent image
FROM eclipse-temurin:21-jre-alpine

# Set the working directory
WORKDIR /app

# Copy the local code to the container
COPY . /app

# Package the application
RUN ./mvnw package

# Run the application
CMD ["java", "-jar", "target/your-app.jar"]