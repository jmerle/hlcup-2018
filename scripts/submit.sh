#!/usr/bin/env bash

echo Building jar
../gradlew clean jar

echo Building container
docker build -t hlcup-2018 .

echo Logging into stor.highloadcup.ru
docker login stor.highloadcup.ru

echo Tagging container
docker tag hlcup-2018 stor.highloadcup.ru/accounts/rich_bat

echo Pushing container
docker push stor.highloadcup.ru/accounts/rich_bat
