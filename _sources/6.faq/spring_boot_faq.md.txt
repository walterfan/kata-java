# spring boot faq
## what are spring boot features?
* Create stand-alone Spring applications
* Embed Tomcat, Jetty or Undertow directly (no need to deploy WAR files)
* Provide opinionated 'starter' dependencies to simplify your build configuration
* Automatically configure Spring and 3rd party libraries whenever possible
* Provide production-ready features such as metrics, health checks, and externalized configuration
* Absolutely no code generation and no requirement for XML configuration

## what the usage of @ConfigurationProperties and @ConfigurationPropertiesScan?

@ConfigurationProperties 和 @ConfigurationPropertiesScan 是 Spring Boot 中用于加载和管理外部配置的注解。它们的主要作用是将外部配置文件（如 application.properties 或 application.yml）中的配置映射到 Java 类中，从而简化配置管理。

1. @ConfigurationProperties

@ConfigurationProperties 用于将配置文件中的一组相关配置映射到一个 Java 类的字段中。

用法

 1. 创建一个 Java 类，并添加 @ConfigurationProperties 注解，指定配置前缀。
 2. 在 Spring 上下文中注册该类，使其成为一个 Bean。

示例

application.yml:
```
app:
  name: MyApp
  version: 1.0
  description: This is a sample application.
```
配置类:
```
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
public class AppConfig {
    private String name;
    private String version;
    private String description;

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
```
使用配置类:
```
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppController {
    private final AppConfig appConfig;

    @Autowired
    public AppController(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    @GetMapping("/info")
    public String getAppInfo() {
        return "App Name: " + appConfig.getName() + 
               ", Version: " + appConfig.getVersion() + 
               ", Description: " + appConfig.getDescription();
    }
}
```
特点

* 类型安全：通过 Java Bean 的字段类型，确保配置值的正确性。
* 支持嵌套：可以通过嵌套类表示更复杂的配置结构。
* 支持校验：结合 @Validated 和 JSR 303 注解（如 @NotNull、@Min）校验配置值。

2. @ConfigurationPropertiesScan

@ConfigurationPropertiesScan 是用于扫描并注册 @ConfigurationProperties 标注的类为 Spring Bean 的注解。

用法

* 默认情况下，@ConfigurationProperties 标注的类需要使用 @Component 或者通过 @Bean 显式注册。
* @ConfigurationPropertiesScan 可以自动扫描指定包下的所有 @ConfigurationProperties 类，无需额外标注 @Component。

示例

将上述 AppConfig 类移除 @Component：

配置类:
```
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public class AppConfig {
    private String name;
    private String version;
    private String description;

    // Getters and setters
}
```
启动类:
```
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@ConfigurationPropertiesScan
public class MyApp {
    public static void main(String[] args) {
        SpringApplication.run(MyApp.class, args);
    }
}
```
特点

* 减少注解冲突：无需在每个配置类上添加 @Component，只需要 @ConfigurationProperties。
* 自动扫描：通过 @ConfigurationPropertiesScan 自动扫描 @ConfigurationProperties 类所在的包及其子包。

两者的主要区别

特性 | @ConfigurationProperties |@ConfigurationPropertiesScan
---|---| ---
作用 | 将配置映射到 Java 类 | 自动扫描并注册所有 @ConfigurationProperties 类
需要额外注解 | 通常需要搭配 @Component 或 @Bean | 无需额外注解，自动扫描
配置注册方式 | 手动注册 | 自动扫描，减少样板代码

推荐使用场景

 1. 简单配置类：直接用 @ConfigurationProperties + @Component。
 2. 多个配置类或复杂配置：用 @ConfigurationPropertiesScan 简化管理，避免逐个注册配置类。

注意事项

* @ConfigurationProperties 类的字段必须有 getter 和 setter 方法。
* 必须确保 Spring Boot 的 spring-boot-configuration-processor 依赖已添加，才能在编译时生成元数据文件，支持 IDE 的代码补全功能。

依赖（build.gradle 或 pom.xml）:
``
dependencies {
    annotationProcessor 'org.springframework.boot:spring-boot-configuration-processor'
}
```


## 配置属性有哪几种方式, 各自的优先级如何?

在 Spring Boot 中，配置属性的优先级决定了当同一属性通过不同方式定义时，哪种方式的值最终生效。Spring Boot 按以下优先级从高到低解析配置属性：

Spring Boot 配置属性优先级（从高到低）

 1. 命令行参数
  * 通过 JVM 启动时提供的命令行参数指定属性值，例如：

java -jar app.jar --server.port=8081


  * 或直接使用 -D 方式设置 JVM 属性：

java -Dserver.port=8081 -jar app.jar


 2. @TestPropertySource 注解
  * 测试类中使用 @TestPropertySource 指定的属性优先级高于默认配置。
 3. @SpringBootTest 中的属性
  * 在 @SpringBootTest 注解中通过 properties 属性指定的值。
 4. Java 系统属性
  * JVM 属性设置，例如：

java -Dserver.port=8081 -jar app.jar


 5. 操作系统环境变量
  * 设置为环境变量的属性，例如：

export SERVER_PORT=8081


 6. RandomValuePropertySource
  * 生成的随机值（如 ${random.int}）会在程序运行时动态注入。
 7. 应用配置文件 (application.properties 或 application.yml)
  * 包含以下几类：
  * 配置文件位置高优先级（外部配置文件优先于内部文件）：
  * file:./config/ (项目根目录 config 文件夹)
  * file:./ (项目根目录)
  * classpath:/config/ (类路径下 config 文件夹)
  * classpath:/ (类路径根目录)
  * 如果多个文件存在且设置相同属性，按加载顺序取优先级最高的值。
  * application-{profile}.properties 文件优先级高于 application.properties。
 8. 默认配置文件 application.properties 或 application.yml 中的默认值
  * 在 application.properties 或 application.yml 文件中定义的值。
 9. 使用 @PropertySource 或 @PropertySources 加载的文件
  * 显式加载的配置文件，例如：

@PropertySource("classpath:custom.properties")


 10. 默认属性
  * 程序中通过代码设置的默认值，例如：

@Value("${server.port:8080}")
private int port;



总结的优先级列表

从高到低优先级：

 1. 命令行参数
 2. @TestPropertySource
 3. @SpringBootTest 的属性
 4. Java 系统属性
 5. 环境变量
 6. RandomValuePropertySource
 7. 外部应用配置文件（application.properties 或 application.yml）
 8. 默认配置文件中的默认值
 9. @PropertySource 显式加载的属性文件
 10. 编码中的默认值

优先级细节补充

  * 如果某些属性值需要优先于配置文件或默认值，可以通过命令行传递或环境变量进行覆盖。
  * 如果一个配置同时存在于多个优先级相同的文件中（如多个 application-{profile}.properties），加载顺序将影响最终结果。
  * 环境变量和属性文件中的属性名不区分大小写，环境变量会自动将 . 替换为 _，例如 SERVER_PORT 会映射为 server.port。

示例：结合优先级的用法

 1. 在 application.properties 中定义：

server.port=8080


 2. 添加环境变量：

export SERVER_PORT=8082


 3. 使用命令行参数运行程序：

java -Dserver.port=8083 -jar app.jar --server.port=8084



最终，server.port 的值为 8084，因为命令行参数的优先级最高。