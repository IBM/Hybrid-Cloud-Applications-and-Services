#!/bin/bash
echo "Installing Docker CLI"
sudo apt-get update
sudo apt-get -y install apt-transport-https ca-certificates curl
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"

echo "Testing Maven build"
cd flight-booking
mvn package
cd ..
mv flight-booking/deployment_artifacts/airlines.war airline_app/apps/

echo "Testing Docker build and Docker compose"
docker build -t hybrid/airlines .
docker-compose up -d
sleep 30s
bash database_init.sh
