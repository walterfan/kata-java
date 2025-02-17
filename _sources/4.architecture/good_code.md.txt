# 什么是好代码

## 1. 好代码必须是看起来很舒服，很干净

象一篇好文章，不罗嗦，容易懂，有头有尾

各个层次，模块及函数分工明确，各司其职, 望文知义

接口即契约，要足够简单，易懂易用， 窄接口好过宽接口

其实只要符合代码规范，命名简单易懂，代码就没那么丑

参见 [Google 的代码风格指南](https://github.com/google/styleguide)

看看重构那本书中的臭味介绍, 可以提高品味

## 2. 好代码要符合基本的编码原则

首先我们先谈谈几个软件开发的普适原则

### KISS

KISS: Keep It Simple and Straight 保持简单和直接, 适当隐藏复杂性

或者

KISS: Keep It Simple and Stupid 保持简单, 象傻瓜一样, 不要让别人多加思考

软件接口或 API 的设计要让人一看就明白, 一看就知道作者的意图和想法, 如何使用它, 有什么结果和可能的异常及副作用. 看过很多代码， 踩过许多坑， 大多是作者或者我自己使用了出乎意料的实现， 不做好必要的抽象和封装， 复杂的判断和算法到处都是

### DRY – Don't Repeat Yourself 
别重复你自己, 如有重复代码, 请抽象或重用

### SRP - Single Responsibility Principle 
单一职责原则: 就一类而言, 应该仅有一个引起它变化的原因

也有一个别称 DOTADIW - Do One Thing and Do It Well 就做一件事并做好它


### OCP - Open Close Principle 
开放封闭原则: 软件实体(类,模块,函数等)应该是可以扩展的, 但是不可修改

### LSP
LSP替换基类原则: 子类型应该可以替换掉它们的基类型

### DIP
DIP依赖倒置原则: 抽象不应该依赖于细节, 细节应该依赖于抽象

### ISP
ISP接口隔离原则: 不应该强迫客户依赖于它们不用的方法, 接口属于客户, 不属于它所在的类层次结构

### REP
REP重用发布等价原则: 重用的粒度就是发布的粒度

### CCP
CCP共同封闭原则: 包中的所有类对于同一类性质的变化应该是共同封闭的. 一个变化若对一个包产生影响, 则将对包中所有的类产生影响, 而对于其他的包不造成任何影响

### CRP
CRP共同重用原则: 一个包中的所有类应该是共同重用的. 如果重用了包中的一个类, 那么就要重用包中的所有类

### ADP
ADP无环依赖原则: 在包的依赖关系图不允许存在环

### SDP
SDP稳定依赖原则: 朝着稳定的方向进行依赖.

### SAP
SAP稳定抽象原则: 包的抽象程度应该和其稳定程度一致

设计模式和面向对象设计中讲了很多，不再赘述

## 3. 好代码要易于理解，测试和修改

封装好复杂性，区分开经常变化与基本不变的代码, 适当抽取易变参数作为配置

还是书里那句话，高内聚，低耦合，依赖倒置

例如最常用的 MVC 模式，为什么我们要分成模型，视图和控制器三块，原因之一就在于分开易变的与不易变的，分开不会在一起变化的部分，减小每次修改的范围。

如果某个方面的功能需要修改，最好是改个配置， 其次是加几行代码或传个不同参数，最差的就是改多个地方，加多个判断， 作霰弹式修改 

## 4. 好代码要考虑周到

各种逻辑流程和意外情况的处理要面面俱到, 单元和模块测试要覆盖异常逻辑和边界

对于服务质量 SLA 要考虑周全, 简单说起来就是满足用户的以下基本需求

1. 功能性
2. 稳定性
3. 可靠性
4. 性能
5. 可维护性
6. 可移植性
7. 灵活性

## 5. 好代码要与时俱进，自我蜕变

人会变老，代码也会，新业务，新技术，新架构，新框架层出不穷，要大胆试验，小心引入，逐步演进，不必抱残守缺，也不要盲目冲动

一般来说，要封装好业务逻辑，核心业务不会大变，即使推到重写也要理解和参照老系统的业务流程

最后，引述一下，Python 之禅

## Python 之禅
虽然说的是Python, 其实适用于多数编程语言


英文                                     | 中文
---------------------------------------- | -------------
Beautiful is better than ugly.           | 美比丑好
	Explicit is better than implicit.    | 明显比隐晦好
	Simple is better than complex.       | 简单比复杂好
	Complex is better than complicated.  | 复杂比难懂好
	Flat is better than nested.			 | 扁平比嵌套好
	Sparse is better than dense.         | 稀疏比稠密好
	Readability counts.                  | 可读性很重要
	Special cases aren't special enough to break the rules.  | 特例也不要打破这个原则
	Although practicality beats purity.                      | 尽管实践会破坏纯洁性
	Errors should never pass silently.                       | 错误还是不能让其悄然滑过
	Unless explicitly silenced.                              | 除非你明确声明不用理会它
	In the face of ambiguity, refuse the temptation to guess. | 别让人来猜测不确定的可能性
	There should be one-- and preferably only one --obvious way to do it. | 应该有一个且只有一个比较好的明显的方法来做事
	Although that way may not be obvious at first unless you're Dutch. | 尽管那个方法可能并非一开始就显而易见
	Now is better than never. | 现在就做比永远不做好
	Although never is often better than right now. | 尽管永远不做经常比马上就动手做好
	If the implementation is hard to explain, it's a bad idea. | 如果实现很难解释清楚, 那它不是一个好主意
	If the implementation is easy to explain, it may be a good idea. | 如果实现很容易说清楚, 那它是个好主意
	Namespaces are one honking great idea – let's do more of those! | 命名空间是个绝妙点子, 让我们那样做得更多
 
简单总结一下

* 文学化编程，想清楚，整明白，胸有成竹，下手千行
* 测试驱动，不做额外的无用功，不追求覆盖率，为的是增强自信心
* 度量驱动，代码上线怎么办，如何易于度量和调优
* 为未来设计有限的灵活性，高聚低耦，无需多改容易扩展
* 考虑周全，足够的健壮，不易出错，不怕出错

## 参考
* Agile Software Development, Principles, Patterns, and Practices by Robert C. Martin
* Clean code by Robert C. Martin
* Refactoring: Improving the Design of Existing Code Martin Fowler