ARG APP_INSIGHTS_AGENT_VERSION=3.4.13

# Application image

FROM hmctspublic.azurecr.io/base/java:openjdk-17-distroless-1.2

COPY lib/applicationinsights.json /opt/app/
COPY build/libs/ia-task-configuration.jar /opt/app/

EXPOSE 4550
CMD [ "ia-task-configuration.jar" ]
