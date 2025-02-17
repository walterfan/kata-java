# JAR

## Introduction

JAR file is a file format based on the popular ZIP file format and is used for aggregating many files into one. A JAR file is essentially a zip file that contains an optional META-INF directory. A JAR file can be created by the command-line jar tool, or by using the java.util.jar API in the Java platform. There is no restriction on the name of a JAR file, it can be any legal file name on a particular platform

## Structure

在 Java 应用程序中，尤其是基于 Spring Boot 的应用程序中，BOOT-INF 和 META-INF 目录是用于组织和存储不同类型资源的关键结构。以下是它们各自的用途：

### META-INF 目录

META-INF 是 JAR 文件的标准目录，用于存放元信息（metadata）和配置文件，与 JAR 文件本身的行为或依赖关系相关。

常见内容

1. MANIFEST.MF 文件
  * 作用: 存储有关 JAR 包的信息，例如版本号、依赖、入口点类等。
  * 关键字段:
  * Main-Class: 指定应用的主类（入口点）。
  * Class-Path: 列出运行时需要的其他 JAR 包。
  * Implementation-Version: 指示版本信息。

2. 服务加载器配置文件 (META-INF/services/)
  * 作用: 定义 SPI（Service Provider Interface）服务实现。例如，META-INF/services/javax.servlet.Servlet 会列出实现 Servlet 接口的类。

3. 其他配置文件
  * 例如 META-INF/spring.factories，用于指定 Spring Boot 自动配置类。
  * META-INF/persistence.xml，用于 JPA 配置。

example of nacos

```
Manifest-Version: 1.0
Created-By: Maven JAR Plugin 3.2.2
Build-Jdk-Spec: 21
Specification-Title: nacos-console 2.4.3
Specification-Version: 2.4
Specification-Vendor: Alibaba Group
Implementation-Title: nacos-console 2.4.3
Implementation-Version: 2.4.3
Implementation-Vendor: Alibaba Group
Main-Class: org.springframework.boot.loader.PropertiesLauncher
Start-Class: com.alibaba.nacos.Nacos
Spring-Boot-Version: 2.7.18
Spring-Boot-Classes: BOOT-INF/classes/
Spring-Boot-Lib: BOOT-INF/lib/
Spring-Boot-Classpath-Index: BOOT-INF/classpath.idx
Spring-Boot-Layers-Index: BOOT-INF/layers.idx


```


### BOOT-INF 目录

BOOT-INF 是 Spring Boot 特有的目录，用于组织 Spring Boot 可执行 JAR 文件的内容。这个目录的结构是 Spring Boot 打包工具（例如 Maven 或 Gradle 插件）生成的。

结构与作用

1. BOOT-INF/classes/
  * 存储内容: 应用程序的编译字节码和资源文件（src/main/resources 和 src/main/java 的输出）。
  * 作用: 是 Spring Boot 应用运行时的主要类路径。

2. BOOT-INF/lib/
  * 存储内容: 应用运行时所需的依赖 JAR 包。
  * 作用: 这些依赖库会添加到类加载器中，用于支持应用运行。

注意:

Spring Boot 将 JAR 包分层组织到 BOOT-INF 目录中，使得：

* 主应用代码（BOOT-INF/classes）与依赖库（BOOT-INF/lib）分离。
* 启动器类加载器（org.springframework.boot.loader.Launcher）能够动态加载这些路径。

两者的区别

特性 | META-INF | BOOT-INF  
---|---|---
标准/特有 | JAR 文件的标准结构 | Spring Boot 特有的结构
存储内容 | JAR 元信息、服务加载器文件等 | 应用程序的类和依赖
目的 | 描述和配置 JAR 包的行为 | 提供应用的实际运行时类与依赖
工具依赖 | 标准 Java 工具（如 java -jar）支持 Spring Boot 的自定义类加载器支持

总结

* META-INF 是标准 JAR 结构的一部分，用于描述 JAR 的元数据。
* BOOT-INF 是 Spring Boot 定义的，用于组织应用代码和依赖关系，使 Spring Boot JAR 能作为独立的可执行单元运行。

## Reference
* https://docs.oracle.com/en/java/javase/17/docs/specs/jar/jar.html
