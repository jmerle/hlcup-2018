#!/usr/bin/env bash

unzip -qq /tmp/data/data.zip -d data

ls
ls data/

java -Xms256m -Xmx2g -jar hlcup-2018.jar
