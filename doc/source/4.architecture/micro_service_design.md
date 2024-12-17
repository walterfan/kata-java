# 微服务设计方法

## 概述

服务就是代表特定功能的软件实体, 是不依赖于任何上下文或外部服务的自治构件.微服务设计就是软件设计的一个子范畴, 它主要是指如何设计这样的服务来满足需求, 而这个服务是微小且自治的, 满足微服务的若干特征.

先让我们看看传统软件设计的流程: 需求分析--概要设计--详细设计

## 软件设计

### 需求分析和整理

对需要做详细的分析, 一是功能性需求

功能性需求: 
* 用例
* 场景
* 验收测试用例

非功能性需求: 
* 高可用性
* 高性能
* 伸缩性
* 扩展性
* 伸缩性
* 安全性
* 稳定性
* 健壮性
* 可测试性

以用户登录与注册为例, 我们来分析其用例 Use case 和用户故事 User Story

### 用例 Use case
除了使用绘图工具和画用例图， 还有几种方法通过脚本来生成用例图

####  一是使用在线网站 yuml.me
https://yuml.me/608ca377

![use case](http://upload-images.jianshu.io/upload_images/1598924-fa230b39b9469c71.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

UML 生成脚本如下
```
[User]-(Sign In)
[User]-(Sign Out)
[User]-(Sign Up)
[User]-(Forget Password)
[User]-(Change Password)
(Sign In)>(Remember Me)
(Sign Up)>(Send Verification Email)
(Forget Password)>(Send Reset Password Email)
(Change Password)<(Send Reset Password Email)
[Admin]^[User]
[Admin]-(Add User)
[Admin]-(Delete User)
[Admin]-(Lock User)
[Admin]-(Change Password Policy)
```

### 二是使用是通过 plantuml 来生成
到 http://plantuml.com/ 上下载 plantuml.jar ， 然后用如下命令生成用例图

java -jar plantuml.jar usecase.txt

示例UML 生成脚本如下
```
@startuml

User -> (Sign In)
User --> (Sign Out) 
User --> (Sign Up)
User --> (activate)
User --> (forget/reset password)
:Admin: ---> (lock user)
:Admin: ---> (add user) 
:Admin: ---> (delete user) 

@enduml
```

###  三是使用graphviz
先安装graphviz, 再运行如下命令

dot usecase1.gv -Tpng -o usecase1.png

示例UML生成脚本如下
```
digraph G {
    rankdir=LR;

    subgraph clusterUser {label="User"; labelloc="b"; peripheries=0; user};
    
    user [shapefile="stick.png", peripheries=0];

    signin [label="Sign In", shape=ellipse];

    signout [label="Sign Out", shape=ellipse];

    signup [label="Sign Up", shape=ellipse];

    user->signin [arrowhead=none];

    user->signout [arrowhead=none];

    user->signup [arrowhead=none];
}
```

### 用户故事 User Story
User Story 讲究 INVEST 原则
* "I" ndependent (of all others) 独立的
* "N" egotiable (not a specific contract for features) 可协商的
* "V" aluable (or [vertical](http://guide.agilealliance.org/guide/incremental.html)) 有价值的
* "E" stimable (to a good approximation) 可估量的
* "S" mall (so as to fit within an iteration) 足够小的
* "T" estable (in principle, even if there isn't a test for it yet) 可测试的

以用户注册 Sign Up 为例, 可以拆分为如下子用户故事

1. 作为一个未注册用户， 我想输入我的电子邮件地址和密码，注册到站点

1.1 我必须输入合法和邮件地址，符合密码策略的密码以及一致的验证码进行注册
      默认的密码策略是最低8个字符， 必须包含大小写字母和至少一个数字

| ## | Story | Priority | Estimation | Deadline| Comments |
|-----|-----|-----|-----|-----|----|
| 1.1.1 | 生成验证码 | P3 | 2 MD | 2018-10-15| 防光学识别 |
| 1.1.2 | 显示注册表单| P1 | 1 MD | 2018-10-10|  |
| 1.1.3 | 邮件地址格式验证| P1 | 1 MD | 2018-10-10| 客户端和服务端都要验证 |
| 1.1.4 | 比较两次输入的密码是否相同| P1 | 2 M
H | 2018-10-10|  |
| 1.1.5 | 验证密码是否符合密码策略| P2 | 1 MD | 2018-10-10|  |
| 1.1.6 | 验证输入的验证码| P3 | 2 MD | 2018-10-12 |  |
| 1.1.7 | 检查是否已有相同的邮件地址存在| P1 | 1 MD | 2018-10-12|  |
| 1.1.8 | 输入验证无误后存入数据库，状态为pending| P2 | 2 MD | 2018-10-13|  |
| 1.1.9 | 生成此用户的激活链接| P2 | 2 MD | 2018-10-15|  |
| 1.1.10 | 向注册邮箱发送一封确认邮件| P2 | 2 MD | 2018-10-15|  |

1.2 我的注册邮箱会收到一封验证邮件， 提示我点击注册连接， 从而激活我的注册帐户

1.3 当我完成激活后会自动跳到站点的首页， 提示我进行登录


### 概要设计

基于上面所定义的验收测试用例, 进行软件服务的总体设计, 

先划分模块，以及模块之间的关系和交互.

先让我们来看看微服务的典型架构 -- 六边形架构（Hexagonal Architecture），又称为端口和适配器架构风格

传统的分层架构我们非常熟悉

1) 表现层
2) 业务层
3) 数据层

而六边形架构更加强调对外提供服务的接口适配
![](https://upload-images.jianshu.io/upload_images/1598924-ad8bdbb86e9d7bb5.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

1) 领域层（Domain Layer）：位于最内层的核心层，纯粹的核心业务逻辑，一般不包含任何技术实现或引用。
2) 端口层（Ports Layer）：领域层之外，负责接收与用例相关的所有请求，这些请求负责在领域层中协调工作。端口层在端口内部作为领域层的边界，在端口外部则扮演了外部实体的角色。
3) 适配器层（Adapters Layer）：端口层之外，负责以某种格式接收输入、及产生输出。

### 详细设计

详细设计就是把总体设计落到实处, 一般我们会绘制如下的 4+1 视图

![](https://upload-images.jianshu.io/upload_images/1598924-c84321f5f77bcc7d.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

1) 逻辑视图（Logical View），设计的对象模型。 
2) 进程视图（Process View），捕捉设计的并发和同步特征。 
3) 部署视图（Deployment View），描述了软件到硬件的映射，反映了分布式特性。 
4) 实现视图（Implementation View），描述了在开发环境中软件的静态组织结构。 

四加一的一是指我们之前提到的用例视图
1) 用例视图（Use-Case View），该视图是其他视图的依据


## 领域驱动服务设计

软件的领域有多广, 微服务的领域就有多广. 基本上我们可分为两大类

1. 非功能领域的通用服务

它指对应于非功能需求的, 通用的, 可重用的服务类型, 比如系统认证服务, 缓存服务, 数据存储服务, 以及在云平台上常用的注册服务, 分布式锁服务, 消息队列服务等等

微服务构建有其自身特点, 尤其是相比单体服务, 分布式系统使得我们在容错和高可用方面必须考虑周详, 有些共通的原则, 模式和实践, 我们在系统设计需要熟练掌握, 并在实践中不断根据度量数据进行演进和调优.

这些和具体的业务关系不大, 无论你是做电商的 还是做网络会议的, 基本上都会用到.

* 服务注册和发现
* 服务网关和编排
* 服务度量和基于度量的自动化运维
* 服务安全,跟踪和审计
* 服务可用性相关模式: 分流, 限流, 断流
* 等等

这些通用模块之后再展开来讲

2. 业务功能领域的专用服务

技术经常更新换代，语言层出不穷，这些都会过时, 淘汰和更新换代, 可以业务逻辑及商业模型不会轻易废弃，因为它是企业安身立命，生存和赚钱的根本，需要小心维护，应用户的需求，企业未来的发展而增强和改进。

而商业模型和业务逻辑如何能映射到软件系统中呢?答案就是领域模型，它是软件设计的核心，指导着我们如何实现，如何编码。

所谓领域主要就是指业务逻辑，规则和流程所对应的的软件设计模型，对于那些重要的，复杂的业务模型称为核心域，相对次要的模型称为支撑子域，这些领域都有一个边界上下文，使用一种通用语言来描述, 而领域的边界，彼此之间的关系以及集成方式使用上下文映射图来表示.

### 领域驱动设计概述


领域驱动设计采用的架构不一而足，视具体案例情况而定。比如分层架构，端口和适配器，SOA，REST，CQRS，事件驱动(管道和过滤器，长时间处理过程，事件源)

领域模型的基础是实体和值对象，而对于它们的处理以及流程控制更适合用领域服务来表示，而领域事件在不同的服务和系统之间用于集成和交互很有用。以模块来划分领域对象，以聚合来整合相关的对象，以工厂来创建对象，以资源库来存取对象，这些都是领域驱动设计中的核心。

每个领域都有其专有的知识体系和业务场景, 服务必然是针对某个业务场景, 直接或者间接地为业务提供服务

1. 使用通用语言
在领域专家和技术人员之间使用统一的语言来描述业务逻辑， 使用规范统一的术语，协议，各种文档和图表

2. 做好领域的划分和建模
领域可分为
* 核心子域：核心的业务逻辑和流程
* 支撑子域：支持核心业务的运转
*通用子域： 基础设施和通用的工具或管理系统， 比如安全验证， 用户管理等

3. 对领域对象进行细分 
* 分析聚合根实体
* 识别根实体和范围和边界

4. 定义限界上下文
* 根据业务场景，领域的划分来定义系统和边界和上下文
微服务与上下游服务的交互与依赖关系


## 微服务设计模板

### 1. 总体介绍

1.1 需求
1.1.1  业务需求和目标
需求分析, 用户场景及用例图

1.1.2 技术需求: 容量需求，高可用性，安全性，伸缩性

1.2 背景 
1.2.1 业务背景
1.2.2 技术背景: 当前架构, 容量, 局限和性能瓶颈
    
### 2. 设计 

2.1 总体架构 
总体框图

2.2 备选方案 

2.3 领域设计 
主要的领域对象, 流程以及实体关系图

2.4 范围与影响
所影响的范围以及对于其他上下游组件的影响

2.5 详细设计 

2.5.1 接口描述 
2.5.2 逻辑描述
2.5.3 数据结构 
2.5.4 局限与限制
2.5.5 性能问题
2.5.6 设计约束
2.5.7 意外情况处理

### 3. 依赖条件 

3.1 平台 
3.2 数据库 
3.3 其他服务及其 SDK 

### 4. 部署 
4.1 配置 
4.2 安装 
4.3 部署及验证

### 5. 度量
5.1 关键因素 KPI 
5.2 度量设计 
5.3 度量工具 

### 6. 测试方案
6.1 测试用例
6.2 API 测试方案
6.3 集成测试和端到端测试方案 
6.4 性能测试方案 

### 7. 问题与风险
当前存在的问题与可能存在的风险

### 8. 参考文档和链接