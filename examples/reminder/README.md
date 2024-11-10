
# prepare database

```
docker exec -it mariadb mysql -u root -p

mysql> create database reminder  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

mysql> create user 'walter'@'%' identified by 'pass1234';

mysql> grant all on reminder.* to 'walter'@'%';

```

* create table
```sql

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL
);
```

# run it

```shell
mvn clean package
mvn spring-boot:run

# or
java 

```

* Create

```shell

curl -X POST http://localhost:8080/users -H "Content-Type: application/json" -d '{"name": "Walter Fan", "email": "walter@fanyamin.com"}'
```

* Read

  - get all users
```shell
curl http://localhost:8080/users
```

  - get a user by ID

```shell
curl http://localhost:8080/users/1
```

* Update

```shell
curl -X PUT http://localhost:8080/users/1 -H "Content-Type: application/json" -d '{"name": "Cherry Chen", "email": "cherry@fanyamin.com"}'
```

* Delete

```shell
curl -X DELETE http://localhost:8080/users/1
```



