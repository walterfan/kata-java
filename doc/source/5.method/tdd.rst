######################
TDD
######################

.. include:: ../links.ref
.. include:: ../tags.ref
.. include:: ../abbrs.ref

============ ==========================
**Abstract** Test Driven Developement
**Category** learning note
**Authors**  Walter Fan
**Status**   WIP as draft
**Updated**  |date|
============ ==========================

.. contents::
   :local:


1. 背景
=======================================

1. 测试的时机
--------------------------------------
不是东西做出来再测，而是测好了东西才能出来 各个大小公司，独立的测试团队都在削减人员，不是测试不重要，而是需要全员从一开始就要做测试， 全过程测试， 早期注重单元测试和组件测试， 后期注重集成测试和端到端测试。在一个迭代尽量做一个相对完整的功能， 及时做集成， 而不是最后再一起做集成测试
(当然独立专业的测试团队还是很有必要的, 自己测自己写的东西, 很难发现意想不到的问题)

2. 测试的效率
--------------------------------------
讲究有效性，稳定性和性能，一定要自动化，跨越多个组件的端到端测试必须有，可是不宜多，而且应该是聚焦在于功能性，无需过度测试，有必要才测， 代码对测试友好， 也就是易于测试。 测试要讲究有效性，稳定性和性能。
从思维定势的角度来看， 单元测试可以自己做， 组件测试和集成测试最好换位思考， 结对测试或交由测试专家来做

3. 测试的度量
--------------------------------------
测试要有覆盖率，哪怕仅作参考 性能测试更要有数据，比如 CPS TPS QPS 最耗时间的函数等在测试过程中收集到的度量数据

4. 测试的对象
--------------------------------------
测试到底测什么?
测试目的要明确, 检查点是先定义好. 哪些值, 状态, 还是行为要检查?
测试范围更要定义清楚, 单元,组件,系统内部还是端到端的用户操作?

测试的分类
======================================
- 黑盒测试
- 白盒测试

---
- 手工测试
- 自动测试

---
- 冒烟测试
- 回归测试
- 探索性测试

---
- 单元测试
- 组件测试
- 消费者测试
- 集成测试
- 端到端测试

---
- 压力测试
- 异常测试

测试金字塔
======================================
![](test_pyramid.png)

因为越往上测试的实现和维护成本就越高, 测试速度也越慢, 所以提倡多写单元测试, 少写端到端的测试, 少写不等于不写, 上层的测试着重测一些主要的路径, 不需要面面俱到, 不然费时费力

- 层次越靠上，运行效率越低，延缓持续集成的构建-反馈循环。
- 层次越靠上，开发复杂度越高，如果团队能力受限，交付进度受影响。
- 端到端测试更容易遇到测试结果的不确定性。
- 层次越靠下，单元隔离性越强，定位分析问题越容易。

为了提交效率，加快交付的频率和速度，使产品具备随时可发布的能力，我们需要构建自动化的测试
- 单元测试
- 集成测试（包括 API 测试）
- 端到端的功能测试

单元测试的原则
=======================================
有本书单元测试之道总结了若干单元测试的原则， 先来回顾一下
Pragmatic Unit Testing by Andrew Hunt and David Thomas

FIRST 原则
---------------------------------------
- Fast 快速的
- Isolated 隔离
- Repeatable 可重复
- Self-Validating 自验证
- Timely 及时的

Right-BICEP 测试原则
---------------------------------------
* Right - Are the results right?	结果是否正确？
* B - are all the boundary conditions correct?	所有的边界条件测试结果正确吗
* I - can you check the inverse relationships?	有没有检查反向关系
* C - can you cross-check results using other means?	能不能用其他方法交叉检查结果？
* E - can you force error conditions to happen?	能不能强制错误情况发生？
* P - are performance characteristics within bounds?	性能特性在不在允许范围之内？

CORRECT 检查原则
---------------------------------------

* C - Conformance - does the value conform to an expected format?	一致性 - 值是否符合预期的格式？
* O - Ordering - is the set of values ordered or unordered as appropriate?	排序 - 值的集合是否根据需要进行排序或无序？
* R - Range - is the value within reasonable minimum and maximum values?	范围 - 值是否在合理的最小值和最大值范围之内？
* R - Reference - does the code reference anything external that isn't under direct control of the code itself?	引用 - 代码是否引用了不受代码本身直接控制的外部依赖？
* E - Existence - does the value exist (e.g. is not null, non-zero, present in a set)?	存在 - 值是否存在（例如，不为null，非零，是否存在于集合中）？
* C - Cardinality - are there exactly enough values?	基数 - 是否有足够的值？
* T - Time (absolute and relative) - is everything happening in order? At the right time? In time?	时间（绝对和相对） - 一切事件是否有序？ 是否在正确的时间？ 是否及时？


测试相关框架与库
=======================================
林林总总, 不胜枚举, C++的googletest, gmock, Java的 Junit, TestNG, Mockito, PowerMock, JavaScript 的 Qunit, Python 的 unittest, Lua 的 luaunit, 等等， 其实他们都是脱胎于 JUnit , 使用起来也是大同小异。

测试的策略
=======================================
测试要分类，分组，而且要能快速定位和诊断测试的失败之处
在开始系统级测试之前, 最好能列一个测试矩阵 Test matrix 把所有已知条件, 场景和输入, 或变化的因子列出来, 以免遗漏， 例如我们要测试网页的登录功能对于各大浏览器的兼容性：

Components	Scenario	Safari	Edge	Chrome	Firefox	Opera
Portal	Login	pass	pass	pass	pass	pass

测试用例的组织
========================================
Given-When-Then
===  ==== ====
BDD  说明  示例
===  ==== ====
Given	先决条件	广告部定制了企业的服务电话语音为最近一次促销活动广告
When	行为或事件	用户拔打 800 服务电话
Then	期待的变化或结果	欢迎声后听到最近一次促销广告
===  ==== ====

测试的过程
========================================
测试四步法
1. Setup
2. Exercise
3. Verify
4. Teardown

![](test_steps.png)