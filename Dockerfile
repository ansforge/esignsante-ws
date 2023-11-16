FROM maven:3-openjdk-17 AS build
COPY settings-docker.xml /usr/share/maven/ref/
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
COPY .openapi-generator-ignore /usr/src/app
RUN mvn -f /usr/src/app/pom.xml -gs /usr/share/maven/ref/settings-docker.xml -DskipTests clean package

FROM openjdk:17-jdk-slim-buster
COPY --from=build /usr/src/app/target/esignsante-*.jar /usr/app/esignsante.jar
USER daemon
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/app/esignsante.jar"]