ARG APP_INSIGHTS_AGENT_VERSION=2.6.4
FROM hmctspublic.azurecr.io/base/java:17-distroless

COPY build/libs/em-annotation-app.jar /opt/app/
COPY lib/AI-Agent.xml /opt/app/

CMD ["em-annotation-app.jar"]

EXPOSE 8080
