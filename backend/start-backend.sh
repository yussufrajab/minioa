#!/bin/bash

echo "Starting CSMS Spring Boot Backend..."
echo ""
echo "Database: PostgreSQL (prizma)"
echo "Profile: Development"
echo "Port: 8080"
echo ""

# Set the active profile to development
export SPRING_PROFILES_ACTIVE=dev

# Start the Spring Boot application
echo "Starting Spring Boot with development profile..."
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev