FROM hmcts/cnp-java-base:openjdk-8u191-jre-alpine3.9-2.0.1

COPY build/libs/em-annotation-app.jar /opt/app/

CMD ["em-annotation-app.jar"]

EXPOSE 8080
