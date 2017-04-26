# Publish your WebSphere Liberty APIs Via API Connect


With IBM® WebSphere® Connect, you can connect your IBM WebSphere Application Server apps and data to the cloud in minutes without rewriting apps or acquiring new data. Designed specifically for your WebSphere Application Server environment, these capabilities give you the flexibility to respond to business changes at scale by using your apps and data wherever they are located, whether on-premises or on the cloud.

This tutorial teaches you how to deploy your on-premises IBM WebSphere Liberty V9 workloads onto the cloud, creating a highly available and sharable API environment. It also discusses in detail how to expose those APIs through IBM API Connect, available free on IBM Bluemix, allowing you to further manage and secure your API assets. You learn the end-to-end process, starting with pushing a WebSphere Liberty package into IBM Bluemix and setting up the API Connect service on IBM Bluemix.

## Included Components

- [WebSphere Liberty](https://developer.ibm.com/wasdev/websphere-liberty/)
- [API Connect](http://www-03.ibm.com/software/products/en/api-connect)


## Steps
1. [Run a WebSphere Liberty API Discovery Server using Docker](#1-run-a-websphere-liberty-api-discovery-server-using-docker)
2. [Create an API Connect service in Bluemix](#2-create-an-api-connect-service-in-bluemix)
3. [Integrate WebSphere Liberty and API Connect: push and pull](#3-integrate-websphere-liberty-and-api-connect-push-and-pull)

[Troubleshooting](#troubleshooting)

# 1. Run a WebSphere Liberty API Discovery Server using Docker

1. First install [Docker CLI](https://www.docker.com/community-edition#/download).

2. Then, go to the defaultServer folder (i.e `cd defaultServer`) and put your **.war** API application in the **apps** folder and config the **server.xml** file. For this example, we will use the airline API application.

	Now, build your server and run it on your local host.

    ```bash
   	docker build -t api-connect .
    docker run -d -p 9085:9085 -p 9448:9448 api-connect
    ```

3. To reach the API Discovery user interface, go to `localhost:9085/ibm/api/explorer`. Since docker only expose tcp port and api-connect is using https port, we need to authenticate the website. Then, use the credentials from your server.xml to login (For this example, the **username** is `user` and the **password** is `demo`).

	You should see something like this in your API Discovery user interface.

	![discovery](images/discovery.png)

4. As shown in the following screen capture, you can click the **Try it out** button, which starts your application, running on the cloud.

	![try it out](images/try-it-out.png)

# 2. Create an API Connect service in Bluemix

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

# 3. Integrate WebSphere Liberty and API Connect: push and pull
> Choose either [push](#31-push-websphere-liberty-apis-into-api-connect) or [pull](#32-pull-websphere-liberty-apis-from-api-connect) WebSphere Liberty APIs from API Connect. Also, push won't work on IBMer's account due to federated reasons.

## 3.1 Push WebSphere Liberty APIs into API Connect

1. Go to `https://<app_name>.mybluemix.net/ibm/api/explorer/`

2. Click **POST** for the apiconnect endpoint

	![post](images/post.png)

3. Fill in the parameters as shown in the following screen capture, your organization ID should be the second part of your Portal URL.

	![parameter](images/parameter.png)

4. You want to publish this API product, not just stage it, so leave the stageOnly parameter as false. The X-APIM-Authorization parameter represents the credentials that Liberty uses to log into API Connect. The description on the right side provides details on the accepted format. The following example uses: apimanager/arthurdm@ca.ibm.com:myPassword.

	![mypassword](images/mypassword.png)

5. The best part about using the Swagger user interface in Liberty to push your APIs into API Connect is that you can use a fully working product.json sample JSON file. Click the sample JSON file under Model Schema, and that JSON file is automatically transferred into the body input box, as shown in the following screen capture:

	![json](images/json.png)

6. Now you're ready to publish these APIs. Click **Try it out!**

	![try](images/try.png)

7. In less than a minute, you should see the operation return successfully (code 200), with the response content, code and headers displayed, as shown in the following screen capture:

	![result](images/result.png)

Congratulation. You API is published. Now explore the API Connect Developer Portal like consumers of your API do. Go to your **Portal URL** and click **API Products**.

Now you can go to your API and try it out at the API Connect Developer Portal.

![api-connect](images/api-connect.png)

## 3.2 Pull WebSphere Liberty APIs from API Connect
1. From the main API Connect dashboard in Bluemix, click the menu icon and select **Drafts**. Click **APIs**, click **Add**, and select **Import API from a file or URL**.

	![import](images/import.png)

2. In the **Import API from a file or URL** window, click **Or import from URL**.

	For the URL, type the Liberty URL that you want to use to import the Swagger document. For this example, you can use `https://<app_name>.mybluemix.net/ibm/api/docs/apiconnect`

3. Click **All APIs** to go back into the main Drafts page, Click **Products**, and then click **Add > New Product**. In the Add a new product window, type in a title (could be anything) and then click **Add**.

4. The design view opens for the Product. Scroll down to the APIs section and click on the + icon. 

	![api](images/api.png)

5. Select the API you just imported, and click **Apply**.

6. In the Plans section, you can create different plans with different rate limits, to control which methods from each API are exposed. For this example, please use the default plan.

	Click the **save icon** to save your changes.

7. Now you are ready to stage your Product into a catalog. Click the **cloud icon** and select the catalog where you want to stage the APIs.

8. To go back into the catalog, click the menu icon , and select **Dashboard**. Then click the menu icon for your staged product and select **Publish**.

	![publish](images/publish.png)

9. In the new window that opens, you can edit who can view your APIs and who can subscribe to your API Plans. For this example, use the defaults and click **Publish**.

Congratulation. You API is published. Now explore the API Connect Developer Portal like consumers of your API do. Go to your **Portal URL** and click **API Products**.

Now you can go to your API and try it out at the API Connect Developer Portal.

![api-connect](images/api-connect.png)

# Troubleshooting

To remove your docker container, run
```bash
docker ps
docker kill <container ID>
docker rm <container ID>
```

# References

This WebSphere API Connect example is based on this developerWorks [article](https://www.ibm.com/developerworks/library/mw-1609-demagalhaes-bluemix-trs/1609-demagalhaes.html).


# License

[Apache 2.0](LICENSE)

