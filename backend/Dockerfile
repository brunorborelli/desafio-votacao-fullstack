FROM maven:3.9.16-eclipse-temurin-21-alpine AS construcao

WORKDIR /aplicacao

COPY pom.xml .
RUN mvn --batch-mode --no-transfer-progress dependency:go-offline

COPY src ./src
RUN mvn --batch-mode --no-transfer-progress clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine

WORKDIR /aplicacao

COPY --from=construcao /aplicacao/target/votacao-backend-*.jar aplicacao.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "aplicacao.jar"]