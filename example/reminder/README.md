# Daily Reminder
## quick start

```bash
./gradlew test
./gradlew bootRun
./gradlew bootBuildImage

```

## flow

```
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

```