######################
Spring Boot Testing
######################

.. include:: ../links.ref
.. include:: ../tags.ref
.. include:: ../abbrs.ref

============ ==========================
**Abstract** Spring Boot Testing
**Authors**  Walter Fan
**Status**   WIP as draft
**Updated**  |date|
============ ==========================

.. contents::
   :local:

overview
=======================
Spring Boot provides a number of utilities and annotations to help when testing your application.

Test support is provided by two modules; spring-boot-test contains core items, and spring-boot-test-autoconfigure supports auto-configuration for tests.

Most developers will just use the spring-boot-starter-test 'Starter'which imports both Spring Boot test modules as well has JUnit, AssertJ, Hamcrest and a number of other useful libraries.


Test scope dependencies
================================
The spring-boot-starter-test starter (in the test scope) contains the following provided libraries:

* JUnit 5: The de-facto standard for unit testing Java applications.
* Spring Test & Spring Boot Test: Utilities and integration test support for Spring Boot applications.
* AssertJ: A fluent assertion library.
* Hamcrest: A library of matcher objects (also known as constraints or predicates).
* Mockito: A Java mocking framework.
* JSONassert: An assertion library for JSON.
* JsonPath: XPath for JSON.
* Awaitility: A library for testing asynchronous systems.

Practice
=================================
* 用 JUnit 5 进行单元测试
* 用 @SpringBootTest 进行集成测试

  将 @SpringBootTest 运用到测试类时, 它会自动引导一个应用的上下文, 扫描 @SpringBootAction 注解的类, 扫描组件

* 用 @WebMvcTest 测试控制器
* 用 @JsonTest 进行 JSON 序列化和反序列化的测试

Reference
================================
* https://docs.spring.io/spring-boot/reference/testing/test-scope-dependencies.html