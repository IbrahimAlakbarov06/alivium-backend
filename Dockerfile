# 1. Base image (Maven + JDK 17)
FROM maven:3.9.2-eclipse-temurin-17 AS build

WORKDIR /app

# 2. Copy pom and download dependencies
COPY pom.xml .
# dependency:go-offline əvəzinə dependency:resolve istifadə et
RUN mvn dependency:resolve dependency:resolve-plugins -B

# 3. Copy source code
COPY src ./src

# 4. Package application
RUN mvn clean package -DskipTests

# 5. Final stage - JRE istifadə et (daha kiçik image)
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]