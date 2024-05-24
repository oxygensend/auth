FROM openjdk:21-jdk

ARG CONFIG=config/config-docker.yaml
ARG PORT=9002

ENV TZ=Europe/Warsaw

HEALTHCHECK --interval=5s --timeout=5s --retries=10 CMD curl -f http://localhost:$PORT/manage/health || exit 1

COPY $CONFIG /app/config.yaml
COPY target/auth.jar  /app/auth.jar


WORKDIR /app

ENTRYPOINT exec java $JAVA_OPTS -jar /app/*.jar --spring.config.additional-location=config.yaml

EXPOSE $PORT

