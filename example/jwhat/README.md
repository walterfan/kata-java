
## init

```bash
mvn archetype:generate -DgroupId=com.fanyamin.bjava -DartifactId=jwhat \
-DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false
mvn clean compile assembly:single
java -jar target/jwhat-1.0-SNAPSHOT-jar-with-dependencies.jar
```


## replace chinese to english puncutation mark

java -cp ./target/jwhat-1.0-SNAPSHOT-jar-with-dependencies.jar com.fanyamin.bjava.util.PunctuationConverter -i <file>