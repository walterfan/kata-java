######################
Property Graph Model
######################

.. include:: ../links.ref
.. include:: ../tags.ref
.. include:: ../abbrs.ref

============ ==========================
**Abstract** Property Graph Model
**Authors**  Walter Fan
**Status**   WIP as draft
**Updated**  |date|
============ ==========================

.. contents::
   :local:

overview
=======================================

A property graph may be defined in graph theoretical terms as a directed, vertex-labeled, edge-labeled multigraph with self-edges, where edges have their own identity. 

In the property graph, we use the term node to denote a vertex, and relationship to denote an edge. 


1. Definitions
=======================================
In a property graph, the following elements may exist:

* Entity
  
  - Node
  - Relationship

* Path

* Token

  - Label
  - Relationship type
  - Property key

* Property

1.1. Entity
-----------------------
* An entity has a unique, comparable identity which defines whether or not two entities are equal (see the Comparability CIP for more details).

* An entity is assigned a set of properties, each of which are uniquely identified in the set by their respective property keys.

1.1.1. Node
~~~~~~~~~~~~~~~~~~~~~~~
* A node is the basic entity of the graph, with the unique attribute of being able to exist in and of itself.
* A node may be assigned a set of unique labels.
* A node may have zero or more outgoing relationships.
* A node may have zero or more incoming relationships.

1.1.2. Relationship
~~~~~~~~~~~~~~~~~~~~~~~
A relationship is an entity that encodes a directed connection between exactly two nodes, the source node and the target node.

An outgoing relationship is a directed relationship from the point of view of its source node.

An incoming relationship is a directed relationship from the point of view of its target node.

A relationship is assigned exactly one relationship type.

1.2. Path
-----------------------
A path represents a walk through a property graph and consists of a sequence of alternating nodes and relationships.

A path always starts and ends at a node.

The smallest possible path contains a single node, and is called an empty path.

A path has a length, which is an integer greater than or equal to zero, which is equal to the number of relationships in the path.

Equality of paths is detailed in the Comparability CIP.

1.3. Token
-----------------------
A token is a nonempty string of Unicode characters.

1.3.1. Label
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
A label is a token that is assigned to nodes only.

1.3.2. Relationship type
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
A relationship type is a token that is assigned to relationships only.

1.3.3. Property key
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
A property key is a token which uniquely identifies an entity’s property.

1.4. Property
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
A property is a pair consisting of a property key and a property value.

A property value is an instantiation of one of Cypher’s concrete, scalar types, or a list of a concrete, scalar type. See the type system CIP for reference.