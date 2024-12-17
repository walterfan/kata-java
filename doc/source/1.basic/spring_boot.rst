######################
Spring Boot
######################

.. include:: ../links.ref
.. include:: ../tags.ref
.. include:: ../abbrs.ref

============ ==========================
**Abstract** Spring Boot
**Authors**  Walter Fan
**Status**   WIP as draft
**Updated**  |date|
============ ==========================

.. contents::
   :local:

overview
=======================
Spring Boot makes it easy to create stand-alone, production-grade Spring based Applications that you can "just run".

We take an opinionated view of the Spring platform and third-party libraries so you can get started with minimum fuss. Most Spring Boot applications need minimal Spring configuration.

If you're looking for information about a specific version, or instructions about how to upgrade from an earlier release, check out the project release notes section on our wiki.

history
=======================
Spring Boot的主要版本之间有诸多重要变化, 以下是一些重点举例说明: 

Spring Boot 1.x
-----------------------
* 起步依赖简化配置: 引入起步依赖, 如spring-boot-starter-web, 自动包含相关依赖, 减少配置。

* 自动配置: 能根据类路径中的依赖自动配置Spring应用上下文。如添加spring-boot-starter-data-jpa, 自动配置JPA相关Bean。

* 内置服务器支持: 默认集成Tomcat, 也可方便切换为Jetty或Undertow, 只需修改配置。

* Actuator监控端点: 提供如/health、/info等监控端点, 查看应用运行状态和信息。

Spring Boot 2.x
-----------------------
* 性能优化: 启动时间大幅缩短, 内存占用降低。如采用懒加载机制, 仅在首次访问时初始化Bean。

* WebFlux集成: 支持响应式编程, 基于Netty等实现非阻塞I/O。如spring-boot-starter-webflux创建响应式Web应用。

* 配置属性绑定增强: 支持更灵活、复杂的配置属性绑定。可将配置属性绑定到嵌套对象或集合, 如myapp.database.host=localhost绑定到@ConfigurationProperties标注的类。

* OAuth 2.0支持增强: 提供更全面的OAuth 2.0客户端和资源服务器支持, 方便与认证服务器集成。

Spring Boot 3.x
-----------------------
* 支持Java 17及以上版本: 充分利用新特性, 如虚拟线程等, 提升性能和并发能力。

* GraalVM原生镜像支持: 可将应用编译为原生可执行文件, 启动更快、内存占用更少, 提高运行效率和资源利用率。

* 配置文件处理优化: 采用更高效的配置文件加载和解析机制, 支持YAML配置文件的多文档块, 按不同环境或场景激活相应配置。

* 依赖升级与优化: 升级众多依赖库版本, 如Spring Framework等, 解决安全漏洞, 引入新功能和性能优化。

Features
=======================
* Create stand-alone Spring applications
* Embed Tomcat, Jetty or Undertow directly (no need to deploy WAR files)
* Provide opinionated 'starter' dependencies to simplify your build configuration
* Automatically configure Spring and 3rd party libraries whenever possible
* Provide production-ready features such as metrics, health checks, and externalized configuration
* Absolutely no code generation and no requirement for XML configuration


Reference
=======================
* https://docs.spring.io/spring-boot/index.html