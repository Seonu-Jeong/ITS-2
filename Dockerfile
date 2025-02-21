# Build stage
FROM amazoncorretto:17 AS builder
WORKDIR /build
COPY . .
RUN ./gradlew build -x test

# Run stage

FROM amazoncorretto:17-alpine

WORKDIR /app
COPY --from=builder /build/build/libs/*.jar app.jar

ENV SPRING_PROFILE "dev"

EXPOSE 8080
ENTRYPOINT ["java", "-Dspring.profiles.active=${SPRING_PROFILE}", "-jar", "app.jar"]