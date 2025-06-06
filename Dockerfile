FROM openjdk:17

# Set the working directory
WORKDIR /app

# Copy the JAR file into the container
COPY target/dns-1.0-SNAPSHOT.jar /app/dns.jar

EXPOSE 53/tcp
EXPOSE 53/udp

# Run the application
CMD ["java", "-jar", "dns.jar"]