######################
Nacos
######################

.. include:: ../links.ref
.. include:: ../tags.ref
.. include:: ../abbrs.ref

============ ==========================
**Abstract** Nacos
**Authors**  Walter Fan
**Status**   WIP as draft
**Updated**  |date|
============ ==========================

.. contents::
   :local:

overview
=======================
Nacos is an easy-to-use dynamic service discovery, configuration and service management platform for building cloud native applications.




concepts
=======================

地域 Region
-----------------------
物理的数据中心，资源创建成功后不能更换。

可用区 Available Zone
-----------------------
同一地域内，电力和网络互相独立的物理区域。同一可用区内，实例的网络延迟较低。

接入点 Endpoint
-----------------------
地域的某个服务的入口域名。

命名空间 Namespace
-----------------------
用于进行租户粒度的配置隔离。不同的命名空间下，可以存在相同的 Group 或 Data ID 的配置。Namespace 的常用场景之一是不同环境的配置的区分隔离，例如开发测试环境和生产环境的资源（如配置、服务）隔离等。

配置 Configuration
-----------------------
在系统开发过程中，开发者通常会将一些需要变更的参数、变量等从代码中分离出来独立管理，以独立的配置文件的形式存在。目的是让静态的系统工件或者交付物（如 WAR，JAR 包等）更好地和实际的物理运行环境进行适配。配置管理一般包含在系统部署的过程中，由系统管理员或者运维人员完成。配置变更是调整系统运行时的行为的有效手段。

配置管理 Configuration Management
-----------------------------------------
系统配置的编辑、存储、分发、变更管理、历史版本管理、变更审计等所有与配置相关的活动。

配置项 Configuration Item
-----------------------------------------
一个具体的可配置的参数与其值域，通常以 param-key=param-value 的形式存在。例如我们常配置系统的日志输出级别（logLevel=INFO|WARN|ERROR） 就是一个配置项。

配置集 Configuration Set
-----------------------------------------
一组相关或者不相关的配置项的集合称为配置集。在系统中，一个配置文件通常就是一个配置集，包含了系统各个方面的配置。例如，一个配置集可能包含了数据源、线程池、日志级别等配置项。

配置集 ID - Data ID
-----------------------
Nacos 中的某个配置集的 ID。配置集 ID 是组织划分配置的维度之一。Data ID 通常用于组织划分系统的配置集。一个系统或者应用可以包含多个配置集，每个配置集都可以被一个有意义的名称标识。Data ID 通常采用类 Java 包（如 com.taobao.tc.refund.log.level）的命名规则保证全局唯一性。此命名规则非强制。

配置分组 Group
-----------------------
Nacos 中的一组配置集，是组织配置的维度之一。通过一个有意义的字符串（如 Buy 或 Trade ）对配置集进行分组，从而区分 Data ID 相同的配置集。当您在 Nacos 上创建一个配置时，如果未填写配置分组的名称，则配置分组的名称默认采用 DEFAULT_GROUP 。配置分组的常见场景：不同的应用或组件使用了相同的配置类型，如 database_url 配置和 MQ_topic 配置。

配置快照 Configuration Snapshot
-----------------------------------------
Nacos 的客户端 SDK 会在本地生成配置的快照。当客户端无法连接到 Nacos Server 时，可以使用配置快照显示系统的整体容灾能力。配置快照类似于 Git 中的本地 commit，也类似于缓存，会在适当的时机更新，但是并没有缓存过期（expiration）的概念。

服务 Service
-----------------------
通过预定义接口网络访问的提供给客户端的软件功能。

服务名 Service Name
-----------------------
服务提供的标识，通过该标识可以唯一确定其指代的服务。

服务注册中心 Service Registry
--------------------------------------
存储服务实例和服务负载均衡策略的数据库。

服务发现 Service Discovery
--------------------------------------
在计算机网络上，（通常使用服务名）对服务下的实例的地址和元数据进行探测，并以预先定义的接口提供给客户端进行查询。

元信息 Metadata
------------------------------------------------------------
Nacos数据（如配置和服务）描述信息，如服务版本、权重、容灾策略、负载均衡策略、鉴权配置、各种自定义标签 (label)，从作用范围来看，分为服务级别的元信息、集群的元信息及实例的元信息。

应用 Application
------------------------------------------------------------
用于标识服务提供方的服务的属性。

服务分组 Service Group
---------------------------------
不同的服务可以归类到同一分组。

虚拟集群 Virtual Cluster
---------------------------------
同一个服务下的所有服务实例组成一个默认集群, 集群可以被进一步按需求划分，划分的单位可以是虚拟集群。

实例 Instance
------------------------------------------------------------
提供一个或多个服务的具有可访问网络地址（IP:Port）的进程。

权重 Weight
------------------------------------------------------------
实例级别的配置。权重为浮点数。权重越大，分配给该实例的流量越大。

健康检查 Health Check
------------------------------------------------------------
以指定方式检查服务下挂载的实例 (Instance) 的健康度，从而确认该实例 (Instance) 是否能提供服务。根据检查结果，实例 (Instance) 会被判断为健康或不健康。对服务发起解析请求时，不健康的实例 (Instance) 不会返回给客户端。

健康保护阈值 Protect Threshold
------------------------------------------------------------
为了防止因过多实例 (Instance) 不健康导致流量全部流向健康实例 (Instance) ，继而造成流量压力把健康实例 (Instance) 压垮并形成雪崩效应，应将健康保护阈值定义为一个 0 到 1 之间的浮点数。当域名健康实例数 (Instance) 占总服务实例数 (Instance) 的比例小于该值时，无论实例 (Instance) 是否健康，都会将这个实例 (Instance) 返回给客户端。这样做虽然损失了一部分流量，但是保证了集群中剩余健康实例 (Instance) 能正常工作。

entry
=======================

.. code-block:: java

   @SpringBootApplication
   @ComponentScan(basePackages = {"com.alibaba.nacos"}, excludeFilters = {@Filter(type = FilterType.CUSTOM, classes = {NacosTypeExcludeFilter.class}), @Filter(type = FilterType.CUSTOM, classes = {TypeExcludeFilter.class}), @Filter(type = FilterType.CUSTOM, classes = {AutoConfigurationExcludeFilter.class})})
   @ServletComponentScan
   public class Nacos {
      public static void main(String[] args) {
         SpringApplication.run(com.alibaba.nacos.Nacos.class, args);
      }
   }


example
=======================

5.1. 服务注册
curl -X POST 'http://127.0.0.1:8848/nacos/v1/ns/instance?serviceName=nacos.naming.serviceName&ip=20.18.7.10&port=8080'

5.2. 服务发现
curl -X GET 'http://127.0.0.1:8848/nacos/v1/ns/instance/list?serviceName=nacos.naming.serviceName'

5.3. 发布配置
curl -X POST "http://127.0.0.1:8848/nacos/v1/cs/configs?dataId=nacos.cfg.dataId&group=test&content=HelloWorld"

5.4. 获取配置
curl -X GET "http://127.0.0.1:8848/nacos/v1/cs/configs?dataId=nacos.cfg.dataId&group=test"

5.5. Nacos控制台页面
打开任意浏览器，输入地址：http://127.0.0.1:8848/nacos，即可进入Nacos控制台页面。



