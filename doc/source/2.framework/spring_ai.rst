######################
Spring AI
######################

.. include:: ../links.ref
.. include:: ../tags.ref
.. include:: ../abbrs.ref

============ ==========================
**Abstract** Spring AI
**Authors**  Walter Fan
**Status**   WIP as draft
**Updated**  |date|
============ ==========================

.. contents::
   :local:

overview
=======================
The Spring AI project aims to streamline the development of applications that incorporate artificial intelligence functionality without unnecessary complexity.

The project draws inspiration from notable Python projects, such as LangChain and LlamaIndex, but Spring AI is not a direct port of those projects.

The project was founded with the belief that the next wave of Generative AI applications will not be only for Python developers but will be ubiquitous across many programming languages.

Features
=======================
* Portable API support across AI providers for Chat, text-to-image, and Embedding models. Both synchronous and streaming API options are supported. Access to model-specific features is also available.

* Support for all major AI Model providers such as Anthropic, OpenAI, Microsoft, Amazon, Google, and Ollama. Supported model types include:

    - Chat Completion

    - Embedding

    - Text to Image

    - Audio Transcription

    - Text to Speech

    - Moderation

* Structured Outputs - Mapping of AI Model output to POJOs.

* Support for all major Vector Database providers such as Apache Cassandra, Azure Cosmos DB, Azure Vector Search, Chroma, Elasticsearch, GemFire, MariaDB, Milvus, MongoDB Atlas, Neo4j, OpenSearch, Oracle, PostgreSQL/PGVector, PineCone, Qdrant, Redis, SAP Hana, Typesense and Weaviate.

* Portable API across Vector Store providers, including a novel SQL-like metadata filter API.

* Tools/Function Calling - permits the model to request the execution of client-side tools and functions, thereby accessing necessary real-time information as required.

* Observability - Provides insights into AI-related operations.

* Document injection ETL framework for Data Engineering.

* AI Model Evaluation - Utilities to help evaluate generated content and protect against hallucinated response.

* Spring Boot Auto Configuration and Starters for AI Models and Vector Stores.

* ChatClient API - Fluent API for communicating with AI Chat Models, idiomatically similar to the WebClient and RestClient APIs.

* Advisors API - Encapsulates recurring Generative AI patterns, transforms data sent to and from Language Models (LLMs), and provides portability across various models and use cases.

* Support for Chat Conversation Memory and Retrieval Augmented Generation (RAG).


Concepts
======================
Model
----------------------

Prompt
----------------------


Embedding
----------------------
嵌入是文本、图像或视频的数字表示，用于捕捉输入之间的关系。

嵌入的工作原理是将文本、图像和视频转换为浮点数数组（称为向量）。这些向量旨在捕捉文本、图像和视频的含义。嵌入数组的长度称为向量的维数。

通过计算两段文本的向量表示之间的数值距离，应用程序可以确定用于生成嵌入向量的对象之间的相似性。


嵌入在实际应用中尤其重要，例如检索增强生成 (RAG) 模式。它们可以将数据表示为语义空间中的点，这类似于欧几里得几何的二维空间，但在更高的维度上。这意味着，就像欧几里得几何中平面上的点可以根据其坐标而接近或远离一样，在语义空间中，点的接近度反映了含义的相似性。

关于相似主题的句子在这个多维空间中的位置更近，就像图上彼此靠近的点一样。这种接近度有助于文本分类、语义搜索甚至产品推荐等任务，因为它允许 AI 根据相关概念在这个扩展的语义景观中的“位置”来辨别和分组相关概念。

Token
----------------------


Structured Output
----------------------


RAG
----------------------


Function Calling
----------------------

Reference
=======================
* https://docs.spring.io/spring-ai/reference