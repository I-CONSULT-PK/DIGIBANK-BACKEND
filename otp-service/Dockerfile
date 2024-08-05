# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the application JAR file into the container at /app
COPY target/otp-service.jar /app/


#Commented out this code will use if needed
# COPY custom-utility.jar /app/

# Expose the port that your application will run on
EXPOSE 9000

# Environment variables for your application configuration
ENV SERVER_PORT=9000 \
    #SPRING_KAFKA_PRODUCER_BOOTSTRAP_SERVERS=kafka:9092 \
#    SPRING_KAFKA_PRODUCER_KEY_SERIALIZER=org.apache.kafka.common.serialization.StringSerializer \
#    SPRING_KAFKA_PRODUCER_VALUE_SERIALIZER=org.springframework.kafka.support.serializer.JsonSerializer \
    SPRING_APPLICATION_NAME=OTP-SERVICE \
    SPRING_DATASOURCE_URL=jdbc:postgresql://192.168.0.196:5432/mydatabase \
    SPRING_DATASOURCE_USERNAME=postgres \
    SPRING_DATASOURCE_PASSWORD=mysecretpassword \
    SPRING_JPA_HIBERNATE_DIALECT=org.hibernate.dialect.PostgreSQL82Dialect \
    #SPRING_JPA_HIBERNATE_DDL_AUTO=update \
    SPRING_JPA_SHOW_SQL=true \
    EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://localhost:8772/eureka \
    EUREKA_CLIENT_FETCH_REGISTRY=true \
    EUREKA_CLIENT_REGISTER_WITH_EUREKA=true \
    EUREKA_INSTANCE_PREFER_IP_ADDRESS=true

# Run the JAR file when the container launches
CMD java -jar /app/otp-service.jar
