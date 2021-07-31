#
# Build stage
#
FROM maven:3.6.0-jdk-11-slim AS build
#FROM adoptopenjdk/openjdk11:alpine
LABEL maintainer="info@virtualan.io"
COPY . /home/app/
RUN mvn -f /home/app/samples/virtualan-virtualization/pom.xml clean install

#
# GCS Mount stage
#
FROM golang:1.15.2-alpine as gcsfuse
RUN apk add --no-cache git
ENV GOPATH /go
RUN go get -u github.com/googlecloudplatform/gcsfuse
RUN apk add --no-cache ca-certificates fuse

#
# Package stage
#
FROM adoptopenjdk/openjdk11:alpine
COPY --from=build /home/app/samples/virtualan-virtualization/lib  /openapi/virtualan/lib
COPY --from=build /home/app/samples/virtualan-virtualization/target/virtualan-virtualization.jar /openapi/virtualan/virtualan-virtualization.jar
COPY --from=gcsfuse /go/bin/gcsfuse /usr/local/bin

ENTRYPOINT ["java", "-cp", "/openapi/virtualan/virtualan-virtualization.jar", "-Dloader.main=io.virtualan.Virtualization",  "org.springframework.boot.loader.PropertiesLauncher"]
