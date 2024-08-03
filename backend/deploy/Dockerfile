# Use an official OpenJDK runtime as a parent image
FROM openjdk:17.0.2-slim

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file into the container
COPY shipintime-0.0.1-SNAPSHOT.jar /app/shipintime.jar

# Expose the port that your application will run on
EXPOSE 15111

# Run the JAR file with placeholders for environment variables
ENTRYPOINT ["sh", "-c", "java -jar /app/shipintime.jar --shipintime.db.ip=${SHIPINTIME_DB_IP} --shipintime.db.port=${SHIPINTIME_DB_PORT} --shipintime.db.name=${SHIPINTIME_DB_NAME} --shipintime.db.username=${SHIPINTIME_DB_USERNAME} --shipintime.db.password=${SHIPINTIME_DB_PASSWORD}"]
