#
# Build stage
#
FROM maven:3.6.0-jdk-11-slim AS build
#FROM adoptopenjdk/openjdk11:alpine
LABEL maintainer="info@virtualan.io"
COPY . /home/app/
RUN mvn -f /home/app/pom.xml clean install

#
# Package stage
#
FROM openjdk:11-jre-slim
COPY --from=build /home/app/samples/virtualan-virtualization/dependency /conf/dependency
COPY --from=build /home/app/samples/virtualan-virtualization/target/virtualan-virtualization.jar /openapi/virtualan/virtualan-virtualization.jar
ENTRYPOINT ["java", "-cp", "/openapi/virtualan/virtualan-virtualization.jar", "-Dloader.main=io.virtualan.Virtualization",  "org.springframework.boot.loader.PropertiesLauncher"]
