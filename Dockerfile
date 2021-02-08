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
FROM adoptopenjdk/openjdk11:alpine
#COPY --from=build /home/app/samples/virtualan-openapi-springdependency /conf/dependency
COPY --from=build /home/app/samples/virtualan-openapi-spring/target/virtualan-virtualization.jar /openapi/virtualan/virtualan-virtualization.jar
ENTRYPOINT ["java", "-cp", "/openapi/virtualan/virtualan-virtualization.jar", "-Dloader.main=io.virtualan.Virtualization",  "org.springframework.boot.loader.PropertiesLauncher"]
