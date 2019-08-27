ARG APP_INSIGHTS_AGENT_VERSION=2.3.1
FROM hmctspublic.azurecr.io/base/java:openjdk-8-distroless-1.1

COPY build/libs/rpa-dg-docassembly.jar lib/applicationinsights-agent-2.3.1.jar lib/AI-Agent.xml /opt/app/

CMD ["em-annotation-app.jar"]

EXPOSE 8080
