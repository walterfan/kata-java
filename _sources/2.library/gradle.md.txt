```{contents} Table of Contents
:depth: 3
```

# Gradle

## Gradle 简介

Gradle 是一个灵活且功能强大的构建工具，主要用于 Java 项目的构建和管理。它继承了 Apache Ant 和 Apache Maven 的优点，同时引入了更高效和可扩展的特性，使其成为现代 Java 开发中不可或缺的工具。

## Gradle 的特点

 1. 基于 Groovy 或 Kotlin 的 DSL（领域特定语言）

  * Gradle 使用 Groovy（或 Kotlin DSL）定义构建脚本，具有良好的可读性和灵活性。
  * 构建脚本简洁且易于维护。

示例：
```
plugins {
    id 'java'
}

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter:3.1.0'
    testImplementation 'org.junit.jupiter:junit-jupiter:5.10.0'
}
```

2. 灵活的插件机制
* Gradle 提供了大量内置插件，如 java、application、maven-publish。
* 可以编写自定义插件，扩展构建过程。

3. 增量构建
* Gradle 能智能判断哪些任务需要重新执行，从而减少不必要的构建步骤，大幅提高构建效率。

4. 强大的依赖管理
* 支持 Maven 和 Ivy 仓库。
* 提供对依赖的版本冲突解析和版本锁定功能。

5. 支持多项目构建
* Gradle 可轻松管理多模块项目，通过 settings.gradle 将子项目纳入管理。
* 支持跨模块的依赖定义和任务共享。

6. 高性能
* Gradle 支持构建缓存和守护进程（Gradle Daemon），能显著加快多次构建速度。
* 支持并行任务执行和远程缓存。

7. 生态系统广泛
* Gradle 是 Android 官方推荐的构建工具，也是 Kotlin 项目默认的构建工具。

## Gradle 的主要概念

1. 项目（Project）
* 每个 Gradle 构建都会处理一个或多个项目，项目可以是代码模块、资源文件夹或其它任务集合。

2. 任务（Task）
* Gradle 的核心单位。每个 Task 是一个可执行的单元，用于完成特定操作，如编译代码、运行测试、打包等。
* 自定义任务示例：

```
task hello {
    doLast {
        println 'Hello, Gradle!'
    }
}
```

3. 依赖（Dependencies）
* 通过声明依赖（dependencies），Gradle 自动解析并下载需要的库。
* 依赖分为多种配置（implementation、testImplementation 等）。

4. 插件（Plugins）
* 插件用于扩展 Gradle 的功能。例如：
* java 插件：支持 Java 编译、测试和打包。
* application 插件：支持可执行程序的打包和运行。
* kotlin 插件：支持 Kotlin 项目构建。

## Gradle 的工作流程

1. 初始化阶段
* Gradle 解析 settings.gradle 文件，确定项目结构。

2. 配置阶段
* Gradle 解析 build.gradle 文件，生成任务依赖图。

3. 执行阶段
* Gradle 按任务依赖图顺序执行任务。

## Gradle 与 Maven 的对比

特性 | Gradle | Maven
---|---|---
配置语言 | Groovy/Kotlin DSL | XML
性能 | 支持增量构建、并行构建、高效缓存 | 较慢，不支持增量构建
灵活性 | 高度可定制 | 相对固定，基于生命周期管理
易用性 | DSL 更简洁，适合复杂项目 | XML 配置清晰，但冗长
插件生态 | 丰富且可编写自定义插件 | 丰富但定制化难度较高

## 适用场景

* 现代化 Java 项目（例如微服务开发）
* 复杂多模块项目（Gradle 对多项目支持非常出色）
* Android 应用开发（Gradle 是 Android Studio 的默认构建工具）

## 快速入门

1. 安装 Gradle
* 下载并安装 Gradle：Gradle 官网
* 使用包管理工具（如 SDKMAN 或 Homebrew）快速安装。

2. 初始化项目：

```
gradle init \
  --type application \
  --dsl groovy \
  --package com.github.walterfan.bjava \
  --test-framework junit \
  --project-name 24game
```

3. 常用命令：
* 构建项目：`gradle build`
* 清理构建：`gradle clean`
* 运行任务：`gradle <task_name>`
* 查看依赖树：`gradle dependencies`

