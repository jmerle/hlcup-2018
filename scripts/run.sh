#!/usr/bin/env bash

echo Building jar
./gradlew clean jar

echo Building container
docker build -t hlcup-2018 .

echo Running container
docker run --rm -it -p "8080:80" -v "$(pwd)/tests/data:/tmp/data" --name hlcup-2018 hlcup-2018
