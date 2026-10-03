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

# Render asigna el puerto real en la variable de entorno PORT; application.properties
# ya lee server.port=${PORT:8080}, asi que no hay que tocar nada mas.
#
# -Djava.net.preferIPv4Stack=true: Render intenta conectar por IPv6 por defecto,
# y la base de datos de Aiven rechaza esa ruta (Connection refused) aunque por
# IPv4 funciona bien. Esto obliga a Java a usar siempre IPv4.
EXPOSE 8080
ENTRYPOINT ["java", "-Djava.net.preferIPv4Stack=true", "-jar", "app.jar"]