# Etapa 1: Construcción (Build)
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copiamos los archivos del proyecto
COPY . .

# Damos permisos de ejecución al wrapper de Maven y compilamos el proyecto
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# Etapa 2: Ejecución (Run)
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copiamos el archivo .jar generado en la etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Exponemos el puerto donde corre Spring Boot (por defecto 8080)
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
