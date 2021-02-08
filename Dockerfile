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
COPY --from=build /home/app/samples/virtualan-openapi-spring/lib  /openapi/virtualan/lib
COPY --from=build /home/app/samples/virtualan-openapi-spring/target/virtualan-virtualization.jar /openapi/virtualan/virtualan-virtualization.jar

## GCS Mount step
FROM golang:1.15.2-alpine as gcsfuse
RUN apk add --no-cache git
ENV GOPATH /go
RUN go get -u github.com/googlecloudplatform/gcsfuse
RUN apk add --no-cache ca-certificates fuse
COPY --from=gcsfuse /go/bin/gcsfuse /usr/local/bin

ENTRYPOINT ["java", "-cp", "/openapi/virtualan/virtualan-virtualization.jar", "-Dloader.main=io.virtualan.Virtualization",  "org.springframework.boot.loader.PropertiesLauncher"]
