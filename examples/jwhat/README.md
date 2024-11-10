
## init

mvn archetype:generate -DgroupId=com.fanyamin.bjava -DartifactId=jwhat \
-DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false
mvn clean compile assembly:single
java -jar target/jwhat-1.0-SNAPSHOT-jar-with-dependencies.jar


