# Imagen de build: compila el proyecto con Maven y Java 17
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B clean package -DskipTests

# Imagen final: solo el jar ya compilado, liviana para correr en Render
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "echo '--- DIAGNOSTICO DE CONEXION A AIVEN ---'; nc -zvw5 mysql-12e3b161-brayanjosua2005-0216.j.aivencloud.com 24907 2>&1 || echo 'nc fallo o no esta disponible'; echo '--- FIN DIAGNOSTICO ---'; exec java -Djava.net.preferIPv4Stack=true -jar app.jar"]