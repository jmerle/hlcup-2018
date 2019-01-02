#!/usr/bin/env bash

echo Building jar
./gradlew clean jar

echo Building container
docker build -t hlcup-2018 .

echo Running container
docker run --rm -dt -p "8080:80" -v "$(pwd)/tests/data:/tmp/data" --name hlcup-2018 hlcup-2018

echo "Waiting until server has prepared"
until curl --output /dev/null --silent --head "http://127.0.0.1:8080/accounts/filter/?query_id=1"; do
    sleep 0.5
done

echo Sourcing Conda
source /home/jasper/.anaconda3/etc/profile.d/conda.sh

echo Ensuring Conda environment exists
if ! conda info --envs | grep "hlcup-2018" &>/dev/null; then
    conda create -n hlcup-2018 python=2.7 requests
fi

echo Activating Conda environment
conda activate hlcup-2018

echo Running tests
python tests/tank.py --ammo_dir=tests

echo Deactivating Conda environment
conda deactivate

echo Killing container
docker container kill hlcup-2018
