ARG APP_INSIGHTS_AGENT_VERSION=3.2.6

# Application image
FROM hmctspublic.azurecr.io/base/java:17-distroless

COPY lib/applicationinsights.json /opt/app/

HEALTHCHECK --interval=10s --timeout=10s --retries=10 CMD http_proxy="" wget -q --spider http://localhost:8080/health || exit 1

CMD ["em-annotation-app.jar"]

EXPOSE 8080
