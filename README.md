# Zob API 🚀

> A Spring Boot backend for a recruitment/job portal platform.

## Tài liệu cho người mới

- [Hướng dẫn đọc dự án chi tiết](docs/huong-dan-doc-du-an.md)

![status](https://img.shields.io/badge/Status-Active-success)
![java](https://img.shields.io/badge/Java-17-blue)
![spring](https://img.shields.io/badge/Spring%20Boot-3.2.4-6DB33F)
![database](https://img.shields.io/badge/Database-MySQL-4479A1)

## ✨ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2.4 |
| API | Spring Web (REST) |
| Security | Spring Security + OAuth2 Resource Server (JWT) |
| Data | Spring Data JPA |
| Validation | Spring Validation |
| Template Engine | Thymeleaf |
| Database | MySQL + MySQL Connector/J |
| Utility | Lombok |
| Filtering/Pagination | SpringFilter JPA |
| Build Tool | Gradle (Kotlin DSL) |

## 🧭 API Overview

Base URL: `/api/v1`

### Public endpoint

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| GET | `/` | Health test endpoint (`Hello World`) | Public |

### Auth APIs

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| POST | `/auth/login` | Login and receive token/cookie | Public |
| GET | `/auth/account` | Get current account info | JWT |
| GET | `/auth/refresh` | Refresh access token | Public |
| POST | `/auth/logout` | Logout and clear refresh cookie | JWT |
| POST | `/auth/register` | Register a new user | Public |
| POST | `/auth/change-password` | Change current user password | JWT |

### User APIs

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| GET | `/users` | Get users with pagination/filter | JWT |
| GET | `/users/{id}` | Get user by id | JWT |
| POST | `/users` | Create user | JWT |
| PUT | `/users` | Update user | JWT |
| DELETE | `/users/{id}` | Delete user | JWT |

### Company APIs

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| GET | `/companies` | Get companies with pagination/filter | Public |
| GET | `/companies/{id}` | Get company by id | Public |
| POST | `/companies` | Create company | JWT |
| PUT | `/companies` | Update company | JWT |
| DELETE | `/companies/{id}` | Delete company | JWT |

### Job APIs

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| GET | `/jobs` | Get jobs with pagination/filter | Public |
| GET | `/jobs/{id}` | Get job by id | Public |
| POST | `/jobs` | Create job | JWT |
| PUT | `/jobs` | Update job | JWT |
| DELETE | `/jobs/{id}` | Delete job | JWT |

### Skill APIs

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| GET | `/skills` | Get skills with pagination/filter | Public |
| POST | `/skills` | Create skill | JWT |
| PUT | `/skills` | Update skill | JWT |
| DELETE | `/skills/{id}` | Delete skill | JWT |

### Role APIs

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| GET | `/roles` | Get roles with pagination/filter | JWT |
| GET | `/roles/{id}` | Get role by id | JWT |
| POST | `/roles` | Create role | JWT |
| PUT | `/roles` | Update role | JWT |
| DELETE | `/roles/{id}` | Delete role | JWT |

### Permission APIs

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| GET | `/permissions` | Get permissions with pagination/filter | JWT |
| POST | `/permissions` | Create permission | JWT |
| PUT | `/permissions` | Update permission | JWT |
| DELETE | `/permissions/{id}` | Delete permission | JWT |

### Resume APIs

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| GET | `/resumes` | Get resumes with pagination/filter | JWT |
| GET | `/resumes/{id}` | Get resume by id | JWT |
| POST | `/resumes` | Create resume | JWT |
| PUT | `/resumes` | Update resume | JWT |
| DELETE | `/resumes/{id}` | Delete resume | JWT |
| POST | `/resumes/by-user` | Get resumes by current user | JWT |

### File APIs

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| POST | `/files` | Upload file | JWT |
| GET | `/files` | Download/view file | JWT |

Security note:
- Public by configuration: `/`, `/api/v1/auth/login`, `/api/v1/auth/refresh`, `/api/v1/auth/register`, `GET /api/v1/companies/**`, `GET /api/v1/jobs/**`, `GET /api/v1/skills/**`.
- All remaining endpoints require JWT.

## ⚙️ Sample application.properties

`src/main/resources/application.properties` is ignored in Git.
Create your local file with this template:

```properties
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
```

## 🏃 Quick Start

1. Create database:

```sql
CREATE DATABASE zob CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. Create `src/main/resources/application.properties` from the sample above.

3. Run the app:

```powershell
.\gradlew.bat bootRun
```

4. Verify:
- `GET http://localhost:8080/` -> `Hello World`

Happy coding 🎯✨
