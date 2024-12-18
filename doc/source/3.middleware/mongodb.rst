######################
MongoDB
######################

.. include:: ../links.ref
.. include:: ../tags.ref
.. include:: ../abbrs.ref

============ ==========================
**Abstract** MongoDB
**Authors**  Walter Fan
**Status**   WIP as draft
**Updated**  |date|
============ ==========================

.. contents::
   :local:

overview
=======================
MongoDB 为文档数据库, 一个集合就相当一张表, 一条记录就是一条文档, 一条文档由若干个键值对组成

数据库类型
=======================
* admin db 权限数据库
* local db 本地数据库, 不会被复制
* config db 保存分片信息
* test db

常用命令
=======================
* 创建自定义数据库 `use dbnane`
* 查看数据库命令 `show dbs`
* 统计数据库信息 `db.stats()`
* 删除数据库命令 `db.dropdatabase()`
* 查看当前数据库下的集合 `db.getCollectionNames()`
* 查看数据库用户角色权限 `show roles`
* 插入一条文档 `db.collection_name.insertOne`
* 插入多条文档 `db.collection_name.insertMany`
* 修改一条文档 `db.collection_name.updateOne`
* 修改多条文档 `db.collection_name.updateMany`
* 删除一条文档 `db.collection_name.deleteOne`
* 删除多条文档 `db.collection_name.deleteOneMany`
* 查找文档 `db.collection_name.find`
* 创建索引 `db.collection_name.createIndex(key:<1/-1>)`