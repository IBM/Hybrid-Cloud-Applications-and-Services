#!/usr/bin/env bash

echo "Installing Bluemix cli"
curl -sL public.dhe.ibm.com/cloud/bluemix/cli/bluemix-cli/Bluemix_CLI_0.5.2_amd64.tar.gz > Bluemix_CLI.tar.gz
tar -xvf Bluemix_CLI.tar.gz
cd Bluemix_CLI
sudo ./install_bluemix_cli

#echo "Installing container service and container registry plugins"
#sudo bx plugin install container-service -r Bluemix
#sudo bx plugin install container-registry -r Bluemix

#curl -LO https://storage.googleapis.com/kubernetes-release/release/$(curl -s https://storage.googleapis.com/kubernetes-release/release/stable.txt)/bin/linux/amd64/kubectl
#echo "Installing kubectl"
#chmod +x ./kubectl
#sudo mv ./kubectl /usr/local/bin/kubectl

sudo add-apt-repository ppa:webupd8team/java
sudo apt-get update
echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | sudo /usr/bin/debconf-set-selections
sudo apt-get install -y oracle-java8-installer

curl -sLo apache-maven-3.5.0-bin.tar.gz http://apache.mirrors.tds.net/maven/maven-3/3.5.0/binaries/apache-maven-3.5.0-bin.tar.gz
sudo tar -zxf apache-maven-3.5.0-bin.tar.gz -C /opt
echo "export PATH=$PATH:/opt/apache-maven-3.5.0/bin" > maven.sh
chmod +x maven.sh
sudo mv maven.sh /etc/profile.d

sudo apt install -y docker-compose
