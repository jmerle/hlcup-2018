FROM openjdk:8-jre-alpine

ENV DOCKER true

RUN mkdir -p /app/data
WORKDIR /app

COPY ./build/libs/hlcup-2018.jar /app/hlcup-2018.jar
COPY ./docker-entrypoint.sh /app/docker-entrypoint.sh

EXPOSE 80

ENTRYPOINT /bin/sh /app/docker-entrypoint.sh
