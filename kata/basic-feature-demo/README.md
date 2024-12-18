
# Java Basic Feature Demo

## create it

```bash
mvn archetype:generate -DgroupId=com.fanyamin.kata -DartifactId=basic-feature-demo \
-DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false
```

## build it

```bash
mvn clean compile assembly:single
```

## run it
```
java -jar target/basic-feature-demo-1.0-SNAPSHOT-jar-with-dependencies.jar
```


## example

* new lambda usage
```
java -cp ./target/basic-feature-demo-1.0-SNAPSHOT-jar-with-dependencies.jar com.fanyamin.kata.demo.PointAvgDistance
```
* replace chinese to english puncutation mark
```
java -cp ./target/basic-feature-demo-1.0-SNAPSHOT-jar-with-dependencies.jar com.fanyamin.kata.util.PunctuationConverter -i <file>
```