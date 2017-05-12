#!/bin/bash
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

echo "Testing Airlines and Database Communication"
curl --user admin:admin -k -X GET --header 'Accept: application/json' 'https://localhost:9443/airlines/'