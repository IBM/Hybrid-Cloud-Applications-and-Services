[![构建状态](https://travis-ci.org/IBM/Hybrid-Cloud-Applications-and-Services.svg?branch=master)](https://travis-ci.org/IBM/Hybrid-Cloud-Applications-and-Services)

# 将您的私有云应用程序和服务扩展到公有云，反之亦然

*阅读本文的其他语言版本：[English](README.md)。*

混合云模型结合了私有云与公有云的要素，让用户能灵活地选择跨企业内部的系统和云来运行应用程序和服务。简单来说，混合模型主要是一个私有云，该私有云允许组织在合理的时间和地点利用共有云。本代码将展示如何向公有云公开企业内部的应用程序和服务，反之亦然。

在本代码中，我们有一个使用 JAX-RS 和 Swagger 注释的企业内部 Java 应用程序，以及使用 CouchDB 的数据库，二者都在受防火墙保护的私有云中运行。我们将演示如何通过利用 Secure Gateway 和 API Connect 等公有云服务，创建一个隧道并向企业防火墙外部公开私有云应用程序和 API。

我们还会将应用程序迁移到公有云，然后介绍如何让在公有云上运行的应用程序可以访问数据库等企业内部的资源。

## 场景
- [场景 1：让私有云中的应用程序可通过公有云从外部进行访问](#scenario-one-enable-your-application-in-private-cloud-to-be-accessed-externally-via-public-cloud)
- [场景 2：让公有云中的应用程序能连接到私有云中的资源](#scenario-two-enable-your-application-in-public-cloud-to-connect-to-resources-in-private-cloud)

![场景](images/hybrid-cloud.png)

## 包含的组件
这些场景是使用以下组件来完成的：
- [Cloud Foundry](https://www.cloudfoundry.org/)
- [CouchDB](http://couchdb.apache.org)
- [WebSphere Liberty](https://developer.ibm.com/wasdev/websphere-liberty/)
- [API Connect](http://www-03.ibm.com/software/products/en/api-connect)
- [Secure Gateway](https://console.ng.bluemix.net/docs/services/SecureGateway/sg_overview.html)
- [Insights for Weather](https://console.ng.bluemix.net/docs/services/Weather/weather_overview.html#about_weather)

## 前提条件

因为我们需要 [Maven](https://maven.apache.org/install.html) 来构建我们的样本应用程序，需要 [Docker](https://www.docker.com/community-edition#/download) 来运行该应用程序和数据库。请在继续阅读[步骤](#steps) 部分之前安装 [Maven](https://maven.apache.org/install.html) 和 [Docker](https://www.docker.com/community-edition#/download)。如果喜欢使用 [Vagrant](https://www.vagrantup.com/) 管理临时环境，项目主目录中包含一个构建文件，可用于创建含 JDK、Maven 和 Docker 的 Ubuntu VM。

## 步骤

### 将企业内部环境连接到公有云
1.[创建一个隧道来将您的企业内部环境连接到公有云](#1-create-a-tunnel-to-connect-your-on-premise-environment-to-public-cloud)

### 场景 1：让私有云中的应用程序可通过公有云从外部进行访问

2.[构建在企业内部运行并使用企业内部的数据库的样本应用程序](#2-build-sample-application-to-run-on-premise-and-use-on-premise-database)

3.[使用 WebSphere Liberty、CouchDB 和 Docker 在企业内部运行应用程序和数据库](#3-run-the-application-and-database-on-premise-using-websphere-liberty-couchdb-and-docker)

### 场景 2：让公有云中的应用程序能连接到私有云中的资源

4.[构建在公有云上运行并使用企业内部的数据库的样本应用程序](#4-build-sample-application-to-run-on-public-cloud-and-use-on-premise-database)

5.[使用 Cloud 在公有云上运行该应用程序，并使用 CouchDB 和 Docker 在企业内部运行数据库](#5-run-the-application-on-public-cloud-using-bluemix-and-database-on-premise-using-couchdb-and-docker)

### 使用 API Connect 编目应用程序 API 并向公众发布
6.[在 Cloud 中创建 API Connect 服务](#6-create-an-api-connect-service-in-bluemix)

7.[集成 WebSphere Liberty 与 API Connect：推送和拉取](#7-integrate-websphere-liberty-and-api-connect-push-and-pull)
- 7.1 [将在 WebSphere 上运行的应用程序 API 推送到 API Connect 中](#71-push-websphere-liberty-apis-into-api-connect)
- 7.2 [从 API Connect 拉取在 WebSphere Liberty 上运行的应用程序 API](#72-pull-websphere-liberty-apis-from-api-connect)

[故障排除](#troubleshooting)

# 1.创建一个隧道来将您的企业内部环境连接到公有云

在这一步中，我们将使用来自 Cloud 的 Secure Gateway 服务，创建一个从企业内部环境到公有云主机的隧道。在该样本中，为了保持配置简单，我们使用了 TCP 协议。Secure Gateway 产品提供了其他协议选项（包括 UDP、HTTP、HTTPS、TLS/SSL），这些协议使用 Secure Gateway 服务为应用程序提供更优秀的安全和身份验证选项。对于包含生产应用程序和数据的解决方案，应基于它们的风险概况对它们进行评估，以选择正确的 Secure Gateway 访问协议和身份验证模式。[这里](https://console.ng.bluemix.net/docs/services/SecureGateway/sg_overview.html#sg_overview)提供了 Secure Gateway 配置的更多细节，可在[这里](https://console.ng.bluemix.net/docs/services/SecureGateway/sg_023.html#sg_023)获取应用程序端和客户端 TLS 设置的示例。我们继续使用 TCP 来展示相关概念。

1.从 Cloud 创建您的 [Secure Gateway 服务](https://console.ng.bluemix.net/catalog/services/secure-gateway?taxonomyNavigation=apis)。

2.然后，按照 [Secure Gateway 入门](https://console.ng.bluemix.net/docs/services/SecureGateway/secure_gateway.html)教程设置您的网关。

3.设置您的 Secure Gateway 客户端时，将 **IBM Installer** 安装在您的私有云上并运行。

![安装程序](images/installer.png)

4.使用您的网关 ID 和安全令牌打开 Secure Gateway 客户端后，您需要添加适用于企业内部端点的访问列表条目。如果您在执行[场景 1：让私有云中的应用程序可通过公有云从外部进行访问](#scenario-one-enable-your-application-in-private-cloud-to-be-accessed-externally-via-public-cloud)，可在您的 Secure Gateway 客户端上运行 `acl allow 127.0.0.1:9443` 来允许访问您的应用服务器。如果您在执行[场景 2：让公有云中的应用程序能连接到私有云中的资源](#scenario-two-enable-your-application-in-public-cloud-to-connect-to-resources-in-private-cloud)，可运行 `acl allow 127.0.0.1:5984` 来允许访问您的数据库。

5.现在返回到 Cloud 的 Secure Gateway 页面，并创建您的目标。首先，在 Guided Setup 上选择 **On-Premises**，并单击 Next。

![企业内部](images/on-premises.png)

6.接下来，输入 **127.0.0.1** 作为您的资源的主机名，输入 **9443**（场景 1）/ **5984**（场景 2）作为您的端口，并单击 Next。

![主机名](images/hostname.png)

7.接下来，选择 **TCP** 作为协议并单击 Next。然后选择 **None** 作为身份验证选项并单击 Next。不要在您的 IP 表规则上添加任何规则。最后，命名您的目标并单击 **Add Destination**。

8.单击您的目标上的**齿轮图标**，查看并记下您的云主机。

![云主机](images/cloud-host.png)

   如果您在执行场景 1，请继续阅读，否则跳到[场景 2](#4-build-sample-application-to-run-on-public-cloud-and-use-on-premise-database)。

# 2.构建在企业内部运行并使用企业内部的数据库的样本应用程序

我们的样本 Airline API 应用程序是一个航空公司订票应用程序，用于演示 API 应用程序如何使用企业内部的数据库存储其数据。

我们还会为该应用程序添加来自公共 Cloud 的 Weather API 凭证，并使用 Maven 构建一个 .war 文件。Weather API 将提供客户选择的目标机场的天气情况。


1.在 Cloud 中创建 [Weather API 服务](https://console.ng.bluemix.net/catalog/services/weather-company-data?taxonomyNavigation=data)。

2.转到您的 Weather API 的 **Service credentials**，记下您的用户名和密码。然后运行 `cd flight-booking/src/main/java/microservices/api/sample` 来转到 sample 目录。现在，将您的用户名和密码凭证添加到 **WeatherAPI.java** 文件中。

![凭证](images/credentials.png)

3.返回到 **flight-booking** 目录，运行 `mvn package` 来构建 .war 文件。


4.现在转到 **deployment_artifacts** 目录，将您的 **airlines.war** 文件转移到主目录的 **airline_app/apps** 文件夹中。


# 3.使用 WebSphere Liberty、CouchDB 和 Docker 在企业内部运行应用程序和数据库

在本例中，我们将使用 WebSphere Liberty 作为应用服务器，使用本地 CouchDB 作为数据库。我们将首先构建我们的应用服务器的 Docker 映像。

在这一步结束后，您应该能通过 localhost 调用您的应用程序 API。

1.要部署 Airline API 应用程序，请将 **.war** 文件放入 **airline_app/apps** 文件夹中并配置 **server.xml** 文件。对于本示例，我们将使用 Airline API 应用程序，但您也可以添加自己的应用程序。

   在这个主目录中，构建您的服务器并在本地主机上运行它。 
  

    docker build -t hybrid/airlines .
    docker-compose up
  
    
   应用服务器和数据库容器将会启动，终端将显示来自您的应用程序的所有日志。

   您的服务器和数据库运行后，打开另一个终端，并运行以下命令来启动 couchDB。

   
    bash database_init.sh
    

2.要进入 WebSphere Liberty API Discovery 用户界面，请访问 `https://localhost:9443/ibm/api/explorer`。接受您看到的有关自签名证书的任何证书警告。使用来自 server.xml 的凭证进行登录（对于本示例，**用户名**为 `admin`，**密码**为 `admin`）。

您应该在 API Discovery 用户界面中看到类似下图的结果。
![API Discovery](images/discovery.png)

3.如下面的屏幕截图所示，可以单击 **Try it out** 按钮，这会启动在 Docker 上运行的样本 Airline 应用程序。

![试用](images/try-it-out.png)

4.现在，访问 `https://<Cloud Host:Port>/ibm/api/explorer/`，验证能从公共 'Cloud Host' 网关服务器访问您的本地服务器接口。请记住，'Cloud Host' 是我们在[第 1 步](#1-create-a-tunnel-to-connect-your-on-premise-enviroment-to-public-cloud) 结束时记下的 Secure Gateway 服务器信息，您的默认用户名为 **admin**，密码为 **admin**。请注意，因为我们在这个样本中使用了 TCP，所以能够访问此 URL 意味着，互联网上的任何系统只要知道该云主机的名称和端口，就能立刻连接到 WebSphere Liberty 应用程序。在生产中，您可能希望结合使用 TLS/SSL 和[相互身份验证](https://console.ng.bluemix.net/docs/services/SecureGateway/sg_023.html#sg_007) 来提高安全性。

请跳到[第 6 步](#6-create-an-api-connect-service-in-bluemix) 来通过 API Connect 公开您的应用程序 API。

# 4.构建在公有云上运行并使用企业内部的数据库的样本应用程序

我们的样本 API 应用程序是一个航空公司订票应用程序，演示了 API 应用程序如何使用企业内部的数据库存储其数据，并使用 Cloud 的 Data Analytic 服务增强其 API 特性。

在这一步中，将为我们的应用程序添加自己的 Weather API 凭证，并使用 Maven 构建我们自己的 .war 文件。


1.创建您的 [Weather API 服务](https://console.ng.bluemix.net/catalog/services/weather-company-data?taxonomyNavigation=data)。Weather API 能为客户提供机场位置和天气情况。


2.转到您的 Weather API 的 **Service credentials**，记下您的用户名和密码。然后运行 `cd flight-booking/src/main/java/microservices/api/sample` 来转到 sample 目录。现在，将您的用户名和密码凭证添加到 **WeatherAPI.java** 文件中。

   ![凭证](images/credentials.png)

3.此外，您需要在 **DatabaseAccess.java** 文件中将数据库地址更改为您的*云主机:端口*。

   ![云主机 2](images/cloud-host2.png)


4.返回到 **flight-booking** 目录，运行 `mvn package` 来构建您的 .war 文件。


5.然后转到 **deployment_artifacts** 目录，并将您的 **airlines.war** 文件转移到主目录的 **airline_app/apps** 文件夹中。


# 5.使用 Cloud 在公有云上运行该应用程序，并使用 CouchDB 和 Docker 在企业内部运行数据库

1.使用 Docker 创建企业内部的数据库。运行以下命令来使用社区的 CouchDB Docker 映像。

    
    docker pull couchdb:latest
    docker run -p 5984:5984 couchdb
    

   然后，通过以下脚本启动 couchDB。

    
    bash database_init.sh
   

2.现在，可以返回到主目录并将您的应用程序推送到云。对于本示例，我们将把我们的应用程序推送到 IBM Cloud Foundry。所以我们需要安装 [Cloud Foundry CLI](https://docs.cloudfoundry.org/cf-cli/install-go-cli.html)。

3.使用以下命令登录到 Cloud Foundry，并将您的应用程序推送到这个云。

   >备注：将 <app_name> 替换为在您的 Cloud 区域中唯一的应用程序名称。这个应用程序名称是您的 API 容器的名称。
 
    cf login -a https://api.ng.bluemix.net
    cf push <app_name> -p airline_app
   

4.要进入应用程序的 API Discovery 用户界面，请访问 https://<app_name>.mybluemix.net/ibm/api/explorer。然后，使用来自 server.xml 的凭证进行登录（对于本示例，**用户名**为 `admin`，**密码**为 `admin`）。

   您应该在 API Discovery 用户界面中看到类似下图的结果。

   ![API Discovery](images/discovery.png)

5.如下面的屏幕截图所示，可以单击 **Try it out** 按钮，这会调用您在 Cloud Foundry 上运行的应用程序和企业内部的数据库。

   ![试用](images/try-it-out.png)

# 6.在 Cloud 中创建 API Connect 服务

在这一步中，我们将设置 API Connect 服务，以帮助我们向公众公开我们的应用程序 API。

1.要将 API Connect 作为 Cloud 服务添加，请访问 Cloud [API Connect 服务](https://console.ng.bluemix.net/catalog/services/api-connect?taxonomyNavigation=services)


2.然后，选择 **Essentials plan** 并单击 **Create**。

3.单击此处转到仪表板

![仪表板](images/dashboard.png)

4.默认情况下，会创建一个名为 **Sandbox** 的空目录。要启用与它对应的开发人员门户，请单击 **Sandbox**，然后单击 **Settings**。

5.单击 **Portal**，然后在 **Portal Configuration** 下选择 **IBM Developer Portal**。这会自动插入一个门户 URL。

6.记下该门户 URL，其中包含稍后需要使用的目标服务器地址和组织。该 URL 可分解为以下 3 部分，如下面的屏幕截图所示：

![门户 URL](images/portal-url.png)

   - 1 是目录的短名称，在这里是 sb。
   - 2 是您的组织 ID，在本例中为 arthurdmcaibmcom-dev。
   - 3 是您的 API Connect 实例的目标地址，例如 https://us.apiconnect.ibmcloud.com。

7.单击右上角的 Save。您会看到以下消息：

    为目录“Sandbox”创建开发者门户可能需要几分钟的时间。 当门户可用时，您将收到一封电子邮件。


8.收到电子邮件后，请访问 **门户 URL**，您会看到类似下图的结果。

![门户](images/portal.png)

企业开发人员可以在这里找到 API 目录中公开的产品（例如一个或一组 API）。开发人员也可以通过博客和论坛链接来相互交流。

# 7.集成 WebSphere Liberty 与 API Connect：推送和拉取
> 选择从 API Connect [推送](#71-push-websphere-liberty-apis-into-api-connect) 或[拉取](#72-pull-websphere-liberty-apis-from-api-connect) WebSphere Liberty API。备注：目前，推送集成不适合使用企业联合 IBM ID 访问 Cloud 的用户。

## 7.1 将 WebSphere Liberty API 推送到 API Connect 中

在这一步中，我们将了解如何在 API Discovery 服务上使用 POST 请求将我们的 API 推送到 API Connect 中。

1.访问 `https://<Cloud Host:Port/app_name>/ibm/api/explorer/`

2.单击 **POST** 寻找 apiconnect 端点

![公告](images/post.png)

3.填入下面的屏幕截图中所示的参数，您的组织 ID 应是门户 URL 的第二部分。

![参数](images/parameter.png)

4.您想要发布这个 API 产品，而不是暂存它，所以应将 stageOnly 参数保留为 false。X-APIM-Authorization 参数表示 Liberty 用来登录 API Connect 的凭证。右侧的描述提供了接受的格式的详细信息。下面的示例使用：`apimanager/bluemixAccount@domain.com:myBluemixPassword`.

![mypassword](images/mypassword.png)


5.对于场景 2，我们可以单击右侧的样本 JSON 文件，并发布您的 API。

对于场景 1，因为我们正在私有云上运行我们的 API，所以不想使用样本 JSON 文件，因为这会将 API 目标 URL 设置为私有云。相反，我们希望将下面的 JSON 文件中的 **target-url**（第 38 行）中的 `<cloud host:port>` 部分（也可以从 **discovery-post.json** 文件获得它）更改为您的云主机:端口（例如 `"https://cap-sg-prd-3.integration.ibmcloud.com:16218$(request.path)"`）。然后将它复制并粘贴到正文输入框中。

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

![json](images/json.png)

6.现在您已准备好发布这些 API。单击 **Try it out!**

![试用](images/try.png)

7.在不到 1 分钟的时间内，您就会看到该操作成功返回（代码为 200），还显示了响应内容、代码和标头，如下面的屏幕截图所示：

![结果](images/result.png)

恭喜您。您的 API 已发布。现在可以像您的 API 用户一样，探索 API Connect 开发人员门户。转到您的**门户 URL** 并单击 **API Products**。

选择您的 API 产品，通过 API Connect 开发人员门户试用它。单击任何 API 调用并使用 **call operation** 按钮试用它。

![api-connect](images/api-connect.png)

## 7.2 从 API Connect 拉取 WebSphere Liberty API

在这一步中，我们将了解如何使用 API Connect 的用户界面，在 API Connect 上创建和管理新的 API 和产品。

1.从 Cloud 中的主要 API Connect 仪表板，单击菜单图标并选择 **Drafts**。依次单击 **APIs**、**Add**，并选择 **Import API from a file or URL**。

![导入](images/import.png)

2.在 **Import API from a file or URL** 窗口中，单击 **Or import from URL**。

对于 URL，键入您想用来导入 Swagger 文档的 Liberty URL。在本例中，您使用了 `https://<Cloud Host:Port/app_name>/ibm/api/docs/apiconnect`。跟之前一样，在本例中，用户名为 **admin**，密码为 **admin**。

3.对于场景 2，不需要执行任何操作。

对于场景 1，因为我们的 API 托管在私有云上，所以我们需要将 API 目标 URL 设置为自己的云主机。因此，导入您的 API 后，转到 **source**。然后转到页面底部（约第 532 行），并将 **target-url** 的值更改为 `'<cloud host:port>$(request.path)'`（将 `<cloud host:port>` 替换为您自己的云主机:端口）。然后单击右上角的**保存图标**。

   ![目标 URL](images/target-url.png)

4.单击 **All APIs** 返回到 Drafts 主页上，单击 **Products**，然后单击 **Add > New Product**。在 Add a new product 窗口中，键入一个标题（可以是任何内容），然后单击 **Add**。

5.该产品的设计视图将会打开。下滚到 APIs 部分并单击 + 图标。

![api](images/api.png)

6.选择刚导入的 API，并单击 **Apply**。

7.在 Plans 部分，可以创建具有不同速率限制的不同计划，以控制公开每个 API 中的哪些方法。对于本示例，请使用默认计划。

单击**保存图标**保存您的更改。

8.现在您已准备好将您的产品暂存到一个目录中。单击**云图标**，并选择您想用来暂存 API 的目录。

9.要返回到该目录，请单击菜单图标，并选择 **Dashboard**。然后单击您暂存的产品的菜单图标，并选择 **Publish**。

![发布](images/publish.png)

10.在打开的新窗口中，可以编辑谁能查看您的 API，谁能订阅您的 API 计划。对于本示例，使用默认设置并单击 **Publish**。

恭喜您。您的 API 已发布。现在可以像您的 API 用户一样，探索 API Connect 开发人员门户。转到您的**门户 URL** 并单击 **API Products**。

选择您的 API 产品，通过 API Connect 开发人员门户试用它。单击任何 API 调用并使用 **call operation** 按钮试用它。

![api-connect](images/api-connect.png)

# 故障排除

要停止您的 Docker-compose 服务，请在这个主目录中运行

```bash
docker-compose down
```

要删除您的 Docker 容器，请运行

```bash
docker ps --all
docker kill <container ID> #run this command if your container is still running
docker rm <container ID>
```

要删除您的 Insights for Weather、API Connect 和 Secure Gateway 服务，请转到您的 IBM Cloud 仪表板。单击**菜单图标**，然后选择 **Delete Service**。

# 参考资料

这个 WebSphere API Connect 示例基于这篇 developerWorks [文章](https://www.ibm.com/developerworks/library/mw-1609-demagalhaes-bluemix-trs/1609-demagalhaes.html)。


# 许可证

[Apache 2.0](LICENSE)
