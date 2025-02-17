######################
Spring MVC
######################

.. include:: ../links.ref
.. include:: ../tags.ref
.. include:: ../abbrs.ref

============ ==========================
**Abstract** Spring MVC
**Authors**  Walter Fan
**Status**   WIP as draft
**Updated**  |date|
============ ==========================

.. contents::
   :local:

overview
=======================
The Spring Framework provides a comprehensive programming and configuration model for modern Java-based enterprise applications - on any kind of deployment platform.

A key element of Spring is infrastructural support at the application level: Spring focuses on the "plumbing" of enterprise applications so that teams can focus on application-level business logic, without unnecessary ties to specific deployment environments.

Features
=======================

.. uml::

   @startuml

   participant HttpClient as client
   participant DispatcherServlet as servlet
   participant HandllerMapping as map
   participant Controller as controller
   participant Service as service

   autonumber

   client->servlet: request
   servlet->map: find router
   map->servlet: controller
   servlet -> controller: process(request)
   controller -> service: process(request)
   service --> controller: response
   controller --> servlet: response
   servlet --> client: response
   @enduml



Reference
========================
* https://docs.spring.io/spring-framework/reference/index.html
