
# video-inventory-management-server

This service handle video management operations

## Prerequisites

* Docker
* Java 17
* MySQL 8
* Maven 3
* redis

## How to build and deploy

# Without Docker

**Execute the following command from top-level.**

```bash with tests
mvn clean install (to use this command change the mysql db connection url like this: spring.datasource.url=jdbc:mysql://localhost:3304/video_inventory)
```

```bash without tests
mvn clean install -DskipTests
```

**Go into `target/` and execute following command**

```bash
java -jar vim-0.0.1-SNAPSHOT.jar
```

## With Docker

**Go into one directory above and execute following command**
```bash
docker-compose up --build vim
```


## Sample CURL Request

#### User register

```json
curl --location --request POST 'http://localhost:8080/user/register' \
--header 'Content-Type: application/json' \
--data-raw '{
"username": "admin",
"password": "password",
"role": "ADMIN"
}'
```

#### login

```json
curl --location --request POST 'http://localhost:8080/auth/login' \
--header 'Content-Type: application/json' \
--data-raw '{
"username": "anwar",
"password": "password"
}'
```