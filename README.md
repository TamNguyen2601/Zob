Zob API
Du an backend cho he thong tuyen dung, xay dung bang Spring Boot.

1) Cong nghe su dung
Java 17
Spring Boot 3.2.4
Spring Web (REST API)
Spring Data JPA
Spring Security + OAuth2 Resource Server (JWT)
Spring Validation
Thymeleaf
MySQL Connector/J
Lombok
SpringFilter JPA (filter + pagination)
Gradle Kotlin DSL
2) Cac API da xay dung
Base URL: /api/v1

Auth

POST /auth/login
GET /auth/account
GET /auth/refresh
POST /auth/logout
POST /auth/register
POST /auth/change-password
User

GET /users
GET /users/{id}
POST /users
PUT /users
DELETE /users/{id}
Company

GET /companies
GET /companies/{id}
POST /companies
PUT /companies
DELETE /companies/{id}
Job

GET /jobs
GET /jobs/{id}
POST /jobs
PUT /jobs
DELETE /jobs/{id}
Skill

GET /skills
POST /skills
PUT /skills
DELETE /skills/{id}
Role

GET /roles
GET /roles/{id}
POST /roles
PUT /roles
DELETE /roles/{id}
Permission

GET /permissions
POST /permissions
PUT /permissions
DELETE /permissions/{id}
Resume

GET /resumes
GET /resumes/{id}
POST /resumes
PUT /resumes
DELETE /resumes/{id}
POST /resumes/by-user
File

POST /files (upload)
GET /files (download/view)
Public test

GET /
Ghi chu security:

Public theo config: /, /api/v1/auth/login, /api/v1/auth/refresh, /api/v1/auth/register, GET /api/v1/companies/, GET /api/v1/jobs/, GET /api/v1/skills/**
Cac API con lai can token JWT.
3) Mau application.properties de chay du an
Luu y: file src/main/resources/application.properties dang bi ignore tren Git. Hay tao file local theo mau duoi day:

spring.application.name=Zob

spring.datasource.url=jdbc:mysql://localhost:3306/zob
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

zob.jwt.base64-secret=replace_with_base64_secret
zob.jwt.access-token-validity-in-seconds=8640000
zob.jwt.refresh-token-validity-in-seconds=8640000
zob.jwt.refresh-token-cookie-secure=false

spring.servlet.multipart.max-file-size=50MB
spring.servlet.multipart.max-request-size=50MB

zob.upload-file.base-uri=file:///C:/path/to/upload/

spring.data.web.pageable.one-indexed-parameters=true
4) Cach chay nhanh
Tao database MySQL:
CREATE DATABASE zob CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
Tao file src/main/resources/application.properties theo mau o tren.

Chay du an:

.\gradlew.bat bootRun
Kiem tra:
GET http://localhost:8080/ -> Hello World