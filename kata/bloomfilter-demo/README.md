## overview

A Bloom filter is a data structure designed to tell you, rapidly and memory-efficiently, whether an element is present in a set.

The price paid for this efficiency is that a Bloom filter is a probabilistic data structure: it tells us that the element either definitely is not in the set or may be in the set.

## usage

```bash
mvn clean package

java -jar ./target/bloomfilter-demo-0.0.1-SNAPSHOT.jar
```