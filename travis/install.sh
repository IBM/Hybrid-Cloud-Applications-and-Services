#!/bin/bash
echo "Testing Maven build"
cd flight-booking
mvn package
cd ..
mv flight-booking/deployment_artifacts/airlines.war airline_app/apps/

echo "Testing Docker build and Docker compose"
docker build -t hybrid/airlines .
docker-compose -f travis/docker-compose-travis.yml up -d
sleep 30s
bash database_init.sh

echo "Testing Airlines and Database Communication"
curl --user admin:admin -k -X GET --header 'Accept: application/json' 'https://localhost:9443/airlines/'

echo " " && echo "Testing Secure Gateway"
curl --user admin:admin -k -X GET --header 'Accept: application/json' 'https://cap-sg-prd-5.integration.ibmcloud.com:17373/airlines/'

#pushing APIs to API Connect
echo " " 
curl --user admin:admin -k -X POST --header 'Content-Type: application/json' \
	--header 'Accept: application/json' \
	--header $cred -d \
	'{"product":"1.0.0","info":{"name":"pushed-product","title":"Test APIs","version":"1.0.0"},"visibility":{"view":{"enabled":true,"type":"public","tags":["string"],"orgs":["string"]},"subscribe":{"enabled":true,"type":"authenticated","tags":["string"],"orgs":["string"]}},"apis":{"liberty":{"name":"liberty-api:1.0.0","x-ibm-configuration":{"assembly":{"execute":[{"invoke":{"target-url":"https://cap-sg-prd-5.integration.ibmcloud.com:17373$(request.path)","title":"Invocation"}}]}}}},"plans":{"default":{"title":"Default Plan","rate-limit":{"value":"100/hour","hard-limit":false},"approval":false}},"createdAt":"2017-05-01T16:13:05.912Z","createdBy":"string"}' \
	'https://cap-sg-prd-5.integration.ibmcloud.com:17373/ibm/api/docs/apiconnect?organization=konnichiwawarudoyahoocom-dev&catalog=Sandbox&server=https://us.apiconnect.ibmcloud.com&stageOnly=false'

echo " " && echo "Testing API Connect"
sleep 60s
curl --user admin:admin -k -X GET --header 'Accept: application/json' 'https://api.us.apiconnect.ibmcloud.com/konnichiwawarudoyahoocom-dev/sb/airlines/'

