FROM eclipse-temurin:22-jdk AS prod

WORKDIR /app

COPY target/*.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]

FROM eclipse-temurin:22-jdk AS dev
WORKDIR /app

COPY .mvn .mvn
COPY mvnw pom.xml ./

RUN ./mvnw -q dependency:go-offline

CMD ["./mvnw", "spring-boot:run"]
