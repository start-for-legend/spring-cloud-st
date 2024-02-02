FROM maven:3.8.3-openjdk-17 AS GRADLE_BUILD

MAINTAINER rubycpp1225@outlook.kr

COPY build.gradle /build/

COPY src /build/src/

WORKDIR /build/

FROM openjdk:17-jdk

VOLUME /tmp

COPY apiEncryptionKey.jks apiEncryptionKey.jks

COPY build/libs/config-service-1.0.jar ConfigService.jar

ENTRYPOINT ["java", "-jar", "ConfigService.jar"]