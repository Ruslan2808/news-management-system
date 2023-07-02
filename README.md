## stack

- Java 17
- Spring Boot
- Spring Web
- Spring Cache
- Spring Data JPA
- Spring Security
- Spring Cloud
- Mapstruct
- Liquibase
- Lombok
- PostgreSQL
- Gradle
- JUnit 5
- AssertJ
- Mockito
- TestContainers

## get started

To run the project you will need *Java 17*, *Spring Boot 3.1*, *Spring Cloud 2022.0.2*, *Testcontainers 1.18.0*, *Mapstruct 1.5.3*, *Gradle 7.6* and *PostgreSQL*

To start the project you need to perform the following steps:
1. pull this project
2. using the *terminal* or *cmd* go to the folder with this project
3. for **```logging-starter```** and **```exception-handling-starter```** type the command ***gradle build*** and wait for it to finish, then type ***gradle publishToMavenLocal*** to publish the starters to the local repository
4. for **```config-server```**, **```auth-service```** and **```news-service```** type the command ***gradle build*** and wait for it to finish
5. open docker and run services in containers with *docker-compose.yml*

In **```news-service```**<br/>
Entities **```News```** and **```Comments```** are in a **```One-To-Many```** relationship, **```News```** may not contain comments.<br/>

## endpoints

### auth-service

The project is running on **```localhost:8081```**

**```POST```** - ***/api/v1/auth/login***
>description - authorization and getting an access token<br>
>url example - ***localhost:8080/api/v1/auth/login***<br>

>request body example:<br>
>```javascript
>{
>  "username": "erik_gibson",
>  "password": "password"
>}

>response body example:<br>
>```javascript
>{
>  "accessToken": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJlcmlrX2dpYnNvbiIsImF1dGhvcml0aWVzIjpbIlJPTEVfQURNSU4iXSwiaWF0IjoxNjg4MzExMTc3LCJleHAiOjE2OTA5MzkxNzd9.gjCvD3SRkwlnTdIyEcG9L9oB1XlHbmDsb1_QhYY6P9Y"
>}

**```POST```** - ***/api/v1/auth/signup***
>description - registering and getting an access token<br>
>url example - ***localhost:8080/api/v1/auth/signup***<br>

>request body example:<br>
>```javascript
>{
>  "username": "ivan_ivanov",
>  "password": "password"
>}

>response body example:<br>
>```javascript
>{
>  "accessToken": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJpdmFuX2l2YW5vdiIsImF1dGhvcml0aWVzIjpbIlJPTEVfSk9VUk5BTElTVCJdLCJpYXQiOjE2ODgzMTExNzcsImV4cCI6MTY5MDkzOTE3N30.0A2RUJlT75PwPubCaNA_NyeYseniWoPkalm0CxY7pwc"
>}

**```GET```** - ***/api/v1/auth/validate***
>description - validating JWT token and providing user data (username and authorities) for authentication<br>
>url example - ***localhost:8080/api/v1/auth/validate***<br>

>request header example: <br>
>```javascript
>"Authorization header": "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJlcmlrX2dpYnNvbiIsImF1dGhvcml0aWVzIjpbIlJPTEVfQURNSU4iXSwiaWF0IjoxNjg4MzExMTc3LCJleHAiOjE2OTA5MzkxNzd9.gjCvD3SRkwlnTdIyEcG9L9oB1XlHbmDsb1_QhYY6P9Y"

>response body example:<br>
>```javascript
>{
>  "username": "erik_gibson",
>  "authorities": [
>       "ROLE_ADMIN"
>  ]
>}

### news-service

The project is running on **```localhost:8080```**

**```GET```** - ***/api/v1/news?title=title&text=text&username=username&page=page&size=size&sort=sort***
>description - getting all news with opportunity pagination, sorting and filtering (all parameters is not required)<br>

>url example - ***localhost:8080/api/v1/news?title=music&text=great&username=norton&page=1&size=5&sort=text,desc***

**```GET```** - ***/api/v1/news?commentText=commentText&page=page&size=size&sort=sort***
>description - getting news by comment text (required) with opportunity pagination and sorting (not required)<br>

>url example - ***localhost:8080/api/v1/news?commentText=great&page=0&size=5&sort=title,asc***

**```GET```** - ***/api/v1/news?commentUsername=commentUsername&page=page&size=size&sort=sort***
>description - getting news by comment username (required) with opportunity pagination and sorting (not required)<br>

>url example - ***localhost:8080/api/v1/news?commentUsername=debbie&page=2&size=2&sort=username,asc***

**```GET```** - ***/api/v1/news/{id}***
>description - getting a news<br>

>url example - ***localhost:8080/api/v1/news/1***

**```GET```** - ***/api/v1/news/{id}/comments?page=page&size=size&sort=sort***
>description - getting news comments<br>

>url example - ***localhost:8080/api/v1/news/1/comments?page=3&size=5&sort=title,desc***

**```GET```** - ***/api/v1/news/{newsId}/comments/{commentId}***
>description - getting a news comment<br>

>url example - ***localhost:8080/api/v1/news/1/comments/2***

**```POST```** - ***/api/v1/news***
>description - saving a news<br>

>request header example: <br>
>```javascript
>"Authorization header": "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJlcmlrX2dpYnNvbiIsImF1dGhvcml0aWVzIjpbIlJPTEVfQURNSU4iXSwiaWF0IjoxNjg4MzExMTc3LCJleHAiOjE2OTA5MzkxNzd9.gjCvD3SRkwlnTdIyEcG9L9oB1XlHbmDsb1_QhYY6P9Y"

>request body example:<br>
>```javascript
>{
>  "title": "Good news",
>  "text": "Good news text"
>}

**```PUT```** - ***/api/v1/news/{id}***
>description - updating a news<br>
>url example - ***localhost:8080/api/v1/news/1***

>request header example: <br>
>```javascript
>"Authorization header": "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJlcmlrX2dpYnNvbiIsImF1dGhvcml0aWVzIjpbIlJPTEVfQURNSU4iXSwiaWF0IjoxNjg4MzExMTc3LCJleHAiOjE2OTA5MzkxNzd9.gjCvD3SRkwlnTdIyEcG9L9oB1XlHbmDsb1_QhYY6P9Y"

>request body example:<br>
>```javascript
>{
>  "title": "Great news",
>  "text": "Great news text"
>}

**```PATCH```** - ***/api/v1/news/{id}***
>description - updating news text<br>
>url example - ***localhost:8080/api/v1/news/1***

>request header example: <br>
>```javascript
>"Authorization header": "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJlcmlrX2dpYnNvbiIsImF1dGhvcml0aWVzIjpbIlJPTEVfQURNSU4iXSwiaWF0IjoxNjg4MzExMTc3LCJleHAiOjE2OTA5MzkxNzd9.gjCvD3SRkwlnTdIyEcG9L9oB1XlHbmDsb1_QhYY6P9Y"

>request body example:<br>
>```javascript
>{
>  "text": "Great news text"
>}

**```DELETE```** - ***/api/v1/news/{id}***
>description - deleting a news<br>
>url example - ***localhost:8080/api/v1/news/1***

>request header example: <br>
>```javascript
>"Authorization header": "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJlcmlrX2dpYnNvbiIsImF1dGhvcml0aWVzIjpbIlJPTEVfQURNSU4iXSwiaWF0IjoxNjg4MzExMTc3LCJleHAiOjE2OTA5MzkxNzd9.gjCvD3SRkwlnTdIyEcG9L9oB1XlHbmDsb1_QhYY6P9Y"

**```GET```** - ***/api/v1/comments?text=text&username=username&page=page&size=size&sort=sort***
>description - getting all comments with opportunity pagination, sorting and filtering (all parameters is not required)<br>

>url example - ***localhost:8080/api/v1/comments?text=record&username=garcia&page=2&size=3&sort=text,asc***

**```GET```** - ***/api/v1/comments/{id}***
>description - getting a comments<br>

>url example - ***localhost:8080/api/v1/comments/1***

**```POST```** - ***/api/v1/comments***
>description - saving a comment<br>

>request header example: <br>
>```javascript
>"Authorization header": "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJlcmlrX2dpYnNvbiIsImF1dGhvcml0aWVzIjpbIlJPTEVfQURNSU4iXSwiaWF0IjoxNjg4MzExMTc3LCJleHAiOjE2OTA5MzkxNzd9.gjCvD3SRkwlnTdIyEcG9L9oB1XlHbmDsb1_QhYY6P9Y"

>request body example:<br>
>```javascript
>{
>  "text": "Good comment",
>  "newsId": "1"
>}

**```PUT```** - ***/api/v1/comments/{id}***
>description - updating a comment<br>
>url example - ***localhost:8080/api/v1/comments/1***

>request header example: <br>
>```javascript
>"Authorization header": "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJlcmlrX2dpYnNvbiIsImF1dGhvcml0aWVzIjpbIlJPTEVfQURNSU4iXSwiaWF0IjoxNjg4MzExMTc3LCJleHAiOjE2OTA5MzkxNzd9.gjCvD3SRkwlnTdIyEcG9L9oB1XlHbmDsb1_QhYY6P9Y"

>request body example:<br>
>```javascript
>{
>  "text": "Great comment",
>}

**```DELETE```** - ***/api/v1/comments/{id}***
>description - deleting a comment<br>
>url example - ***localhost:8080/api/v1/news/1***

>request header example: <br>
>```javascript
>"Authorization header": "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJlcmlrX2dpYnNvbiIsImF1dGhvcml0aWVzIjpbIlJPTEVfQURNSU4iXSwiaWF0IjoxNjg4MzExMTc3LCJleHAiOjE2OTA5MzkxNzd9.gjCvD3SRkwlnTdIyEcG9L9oB1XlHbmDsb1_QhYY6P9Y"
