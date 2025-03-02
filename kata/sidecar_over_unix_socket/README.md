# Lazy Rabbit Reminder

Reminding myself to return book, or doing something else.

This is still work in progress...

## quick start

```bash
./gradlew test
./gradlew bootRun
./gradlew bootBuildImage

```

## run and test
* start it
```shell

./gradlew bootRun

# or

./gradlew bootJar
java -jar build/libs/reminder-0.0.1-SNAPSHOT.jar
```
* test it

```bash
curl http://localhost:2024
curl http://localhost:2024/books
```

* h2 console

```bash
open http://localhost:2024/h2-console
```

