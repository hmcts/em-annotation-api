FROM hmcts/cnp-java-base:openjdk-8u181-jre-alpine3.8-1.0

ENV APP em-annotation-app.jar
ENV APPLICATION_TOTAL_MEMORY 512M
ENV APPLICATION_SIZE_ON_DISK_IN_MB 53

RUN mkdir -p /opt/app

COPY build/libs/$APP /opt/app/

EXPOSE 8080