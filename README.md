# Publish your WebSphere Liberty APIs Via API Connect


With IBM® WebSphere® Connect, you can connect your IBM WebSphere Application Server apps and data to the cloud in minutes without rewriting apps or acquiring new data. Designed specifically for your WebSphere Application Server environment, these capabilities give you the flexibility to respond to business changes at scale by using your apps and data wherever they are located, whether on-premises or on the cloud.

This tutorial teaches you how to deploy your on-premises IBM WebSphere Liberty on Docker. Then use IBM Secure Gateway service to create a secure, highly available, and sharable API environment. It also discusses in detail how to expose those APIs through IBM API Connect, available free on IBM Bluemix, allowing you to further manage and secure your API assets. You learn the end-to-end process, starting with pushing a WebSphere Liberty package into IBM Bluemix and setting up the API Connect service on IBM Bluemix.

## Included Components

- [WebSphere Liberty](https://developer.ibm.com/wasdev/websphere-liberty/)
- [API Connect](http://www-03.ibm.com/software/products/en/api-connect)
- [Secure Gateway](https://console.ng.bluemix.net/docs/services/SecureGateway/sg_overview.html)


## Steps
1. [Run a WebSphere Liberty API Discovery Server using Docker](#1-run-a-websphere-liberty-api-discovery-server-using-docker)
2. [Create Secure Gateway to securely expose your APIs](#2-create-secure-gateway-to-securely-expose-your-apis)
3. [Create an API Connect service in Bluemix](#3-create-an-api-connect-service-in-bluemix)
4. [Integrate WebSphere Liberty and API Connect: push and pull](#4-integrate-websphere-liberty-and-api-connect-push-and-pull)
- 4.1 [Push WebSphere Liberty APIs into API Connect](#41-push-websphere-liberty-apis-into-api-connect)
- 4.2 [Pull WebSphere Liberty APIs from API Connect](#42-pull-websphere-liberty-apis-from-api-connect)

[Troubleshooting](#troubleshooting)

# 1. Run a WebSphere Liberty API Discovery Server using Docker

In this step, we want to put all our APIs in one place. Then, we will build our own WebSphere Liberty API Discovery Server docker image with all those APIs and run it on docker. At the end of this step, you should able to call your APIs via your localhost.

1. First install [Docker CLI](https://www.docker.com/community-edition#/download).

2. Then, go to the defaultServer folder (i.e `cd defaultServer`) and put your **.war** API application in the **apps** folder and config the **server.xml** file. For this example, we will use the airline API application.

	Now, in your defaultServer folder, build your server and run it on your local host.

    ```bash
   	docker build -t api-connect .
    docker run -d -p 9085:9085 -p 9448:9448 api-connect
    ```

3. To reach the API Discovery user interface, go to `https://localhost:9448/ibm/api/explorer`. Since docker only expose tcp port and api-connect is using https port, we need to authenticate the website. Then, use the credentials from your server.xml to login (For this example, the **username** is `user` and the **password** is `demo`).

	You should see something like this in your API Discovery user interface.

	![discovery](images/discovery.png)

4. As shown in the following screen capture, you can click the **Try it out** button, which starts your application, running on Docker

	![try it out](images/try-it-out.png)
    
    
# 2. Create Secure Gateway to securely expose your APIs.

In this step, we will create a secure gateway to expose our localhost APIs on cloud host. At the end of this step, you should able to access and call your localhost APIs on any device via cloud host.

1. First, create your [secure gateway service](https://console.ng.bluemix.net/catalog/services/secure-gateway?taxonomyNavigation=apis) from bluemix.

2. Then, follow this [Getting started with the Secure Gateway](https://console.ng.bluemix.net/docs/services/SecureGateway/secure_gateway.html) tutorial to setup your gateway.

3. When you setup your secure gateway client, install **IBM Installer** and run it on your local machine.

	![installer](images/installer.png)
    
4. After you open the secure gateway client with your Gateway ID and Security Token, click enter and run the following commands.

	```
    acl allow 127.0.0.1:9448
    ```

	Now your Gateway is able to access your API Discovery Server.

5. Now let's create the destination for our gateway. First, select **On-Premises** at Guided Setup and click next. 

	![on-premises](images/on-premises.png)
    
6. Next, put down **127.0.0.1** for your resource hostname and **9448** for your port and click next.

	![hostname](images/hostname.png)
    
7. Next, select **TCP** for your protocol and click next. Then, select **None** for your authentication and click next. Then, do not put anything on your IP table rules. Lastly, name your destination and click **Add Destination**.


8. Now, you can access your API Discovery Server via the secure gateway cloud host. You can view your cloud host by clicking on the **gear icon** on your destination. 

	![cloud-host](images/cloud-host.png)


9. Now, go to `https://<Cloud Host:Port>/ibm/api/explorer/` and varify the secure gateway is working. Remember, your default username is **user** and password is **demo**. 



# 3. Create an API Connect service in Bluemix

In this step, we will setup API Connect service to help us distribute our APIs.

1. To add API Connect as a Bluemix service, go to the bluemix [API Connect service](https://console.ng.bluemix.net/catalog/services/api-connect?taxonomyNavigation=services)


2. Then, select the **Essentials plan** and click **Create**.

3. Go to dashboard by clicking here

	![dashboard](images/dashboard.png)

4. By default, an empty catalog called **Sandbox** is created. To enable its corresponding developer portal, click **Sandbox** then **Settings**.

5. Click **Portal**, and then under **Portal Configuration**, select **IBM Developer Portal**. A Portal URL is automatically inserted.

6. Take note of the Portal URL, which reveals the target server address and organization that you need later. The URL is broken down into the following three parts, as shown in the following screen capture: 

	![portal-url](images/portal-url.png)

    - 1 is the catalog's short name, in this case, sb.
    - 2 is your organization ID, in the example, arthurdmcaibmcom-dev.
    - 3 is the target address of your API Connect instance, for example, https://us.apiconnect.ibmcloud.com. 

7. Click Save at the top right corner. You see the following message:

    `
    Creating the developer portal for catalog 'Sandbox' may take a few minutes. You will receive an email when the portal is available.
    `

8. After you received the email, go to the **Portal URL** and you will see something like this.

	![portal](images/portal.png)

	This is where enterprise developers go to find the products (for example, an API or a group of APIs) that are exposed in the API catalog. Developers also can interact with each other through the Blogs and Forums links.

# 4. Integrate WebSphere Liberty and API Connect: push and pull
> Choose either [push](#41-push-websphere-liberty-apis-into-api-connect) or [pull](#42-pull-websphere-liberty-apis-from-api-connect) WebSphere Liberty APIs from API Connect. Also, push won't work on IBMer's account due to federated reasons.

## 4.1 Push WebSphere Liberty APIs into API Connect

In this step, we will learn about how to use the post request on API discovery to push our APIs into API Connect.

1. Go to `https://<Cloud Host:Port>/ibm/api/explorer/`

2. Click **POST** for the apiconnect endpoint

	![post](images/post.png)

3. Fill in the parameters as shown in the following screen capture, your organization ID should be the second part of your Portal URL.

	![parameter](images/parameter.png)

4. You want to publish this API product, not just stage it, so leave the stageOnly parameter as false. The X-APIM-Authorization parameter represents the credentials that Liberty uses to log into API Connect. The description on the right side provides details on the accepted format. The following example uses: apimanager/arthurdm@ca.ibm.com:myPassword.

	![mypassword](images/mypassword.png)

5. Since we are running our APIs on our local machine, we do not want to use the sample JSON file because that will set the APIs target URL to our local machine. Instead, we want to change the `<cloud host:port>` part in **target-url** (line 38) from the following JSON file (you can also get it from the **discovery-post.json** file) to your cloud host : port (e.g. `"https://cap-sg-prd-1.integration.ibmcloud.com:16218$(request.path)"`). Then copy and paste it into the body input box.
	```JSON
	{
	  "product": "1.0.0",
	  "info": {
	    "name": "pushed-product",
	    "title": "A Product that encapsulates Liberty APIs",
	    "version": "1.0.0"
	  },
	  "visibility": {
	    "view": {
	      "enabled": true,
	      "type": "public",
	      "tags": [
	        "string"
	      ],
	      "orgs": [
	        "string"
	      ]
	    },
	    "subscribe": {
	      "enabled": true,
	      "type": "authenticated",
	      "tags": [
	        "string"
	      ],
	      "orgs": [
	        "string"
	      ]
	    }
	  },
	  "apis": {
	    "liberty": {
	      "name": "liberty-api:1.0.0",
	      "x-ibm-configuration": {
	        "assembly": {
	          "execute": [
	            {
	              "invoke": {
	                "target-url": "<cloud host:port>$(request.path)",
	                "title": "Invocation"
	              }
	            }
	          ]
	        }
	      }
	    }
	  },
	  "plans": {
	    "default": {
	      "title": "Default Plan",
	      "rate-limit": {
	        "value": "100/hour",
	        "hard-limit": false
	      },
	      "approval": false
	    }
	  },
	  "createdAt": "2017-05-01T16:13:05.912Z",
	  "createdBy": "string"
	}
	```
	![json](images/json.png)

6. Now you're ready to publish these APIs. Click **Try it out!**

	![try](images/try.png)

7. In less than a minute, you should see the operation return successfully (code 200), with the response content, code and headers displayed, as shown in the following screen capture:

	![result](images/result.png)

Congratulation. You API is published. Now explore the API Connect Developer Portal like consumers of your API do. Go to your **Portal URL** and click **API Products**.

Now you can go to your API and try it at the API Connect Developer Portal. Click any API call and try it using the **call operation** button.

![api-connect](images/api-connect.png)

## 4.2 Pull WebSphere Liberty APIs from API Connect

In this step, we will learn about how to create and manage new APIs and products on API connect using API connect's user interface.

1. From the main API Connect dashboard in Bluemix, click the menu icon and select **Drafts**. Click **APIs**, click **Add**, and select **Import API from a file or URL**.

	![import](images/import.png)

2. In the **Import API from a file or URL** window, click **Or import from URL**.

	For the URL, type the Liberty URL that you want to use to import the Swagger document. For this example, you can use `https://<Cloud Host:Port>/ibm/api/docs/apiconnect`. Remember the username for this example is **user** and password is **demo**.
    
3. After you imported your API, go to **source**. Then go to the bottom of the page (around line 532) and change the **target-url**'s value to `'<cloud host:port>$(request.path)'` (replace `<cloud host:port>` to your own cloud host:port). Then click the **save icon** on the top right corner.

    ![target-url](images/target-url.png)

4. Click **All APIs** to go back into the main Drafts page, Click **Products**, and then click **Add > New Product**. In the Add a new product window, type in a title (could be anything) and then click **Add**.

5. The design view opens for the Product. Scroll down to the APIs section and click on the + icon. 

	![api](images/api.png)

6. Select the API you just imported, and click **Apply**.

7. In the Plans section, you can create different plans with different rate limits, to control which methods from each API are exposed. For this example, please use the default plan.

	Click the **save icon** to save your changes.

8. Now you are ready to stage your Product into a catalog. Click the **cloud icon** and select the catalog where you want to stage the APIs.

9. To go back into the catalog, click the menu icon , and select **Dashboard**. Then click the menu icon for your staged product and select **Publish**.

	![publish](images/publish.png)

10. In the new window that opens, you can edit who can view your APIs and who can subscribe to your API Plans. For this example, use the defaults and click **Publish**.

Congratulation. You API is published. Now explore the API Connect Developer Portal like consumers of your API do. Go to your **Portal URL** and click **API Products**.

Now you can go to your API and try it at the API Connect Developer Portal. Click any API call and try it using the **call operation** button.

![api-connect](images/api-connect.png)

# Troubleshooting

To remove your docker container, run
```bash
docker ps
docker kill <container ID>
docker rm <container ID>
```

To remove your API connect and Secure Gateway service, go to your IBM Bluemix dashboard. Then click the menu icon and then select Delete Service.

# References

This WebSphere API Connect example is based on this developerWorks [article](https://www.ibm.com/developerworks/library/mw-1609-demagalhaes-bluemix-trs/1609-demagalhaes.html).


# License

[Apache 2.0](LICENSE)
