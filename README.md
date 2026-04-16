# 🚀 Zob API

Backend cho hệ thống tuyển dụng, xây dựng bằng Spring Boot.

![Java](https://img.shields.io/badge/Java-17-red?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.4-6DB33F?logo=springboot)
![Gradle](https://img.shields.io/badge/Gradle-Kotlin%20DSL-02303A?logo=gradle)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql)

## 📚 Mục lục

- [1. Công nghệ sử dụng](#1-công-nghệ-sử-dụng)
- [2. Danh sách API](#2-danh-sách-api)
- [3. Security note](#3-security-note)
- [4. Mẫu application.properties](#4-mẫu-applicationproperties)
- [5. Chạy nhanh dự án](#5-chạy-nhanh-dự-án)

## 1. Công nghệ sử dụng

- ☕ Java 17
- 🌱 Spring Boot 3.2.4
- 🌐 Spring Web (REST API)
- 🗃️ Spring Data JPA
- 🔐 Spring Security + OAuth2 Resource Server (JWT)
- ✅ Spring Validation
- 🎨 Thymeleaf
- 🐬 MySQL Connector/J
- 🧩 Lombok
- 🔎 SpringFilter JPA (filter + pagination)
- 🛠️ Gradle Kotlin DSL

## 2. Danh sách API

Base URL: `/api/v1`

### 🔑 Auth

| Method | Endpoint | Mô tả nhanh |
|---|---|---|
| POST | `/auth/login` | Đăng nhập |
| GET | `/auth/account` | Lấy thông tin tài khoản hiện tại |
| GET | `/auth/refresh` | Refresh token |
| POST | `/auth/logout` | Đăng xuất |
| POST | `/auth/register` | Đăng ký |
| POST | `/auth/change-password` | Đổi mật khẩu |

### 👤 User

| Method | Endpoint | Mô tả nhanh |
|---|---|---|
| GET | `/users` | Lấy danh sách user |
| GET | `/users/{id}` | Lấy chi tiết user |
| POST | `/users` | Tạo user |
| PUT | `/users` | Cập nhật user |
| DELETE | `/users/{id}` | Xóa user |

### 🏢 Company

| Method | Endpoint | Mô tả nhanh |
|---|---|---|
| GET | `/companies` | Lấy danh sách công ty |
| GET | `/companies/{id}` | Lấy chi tiết công ty |
| POST | `/companies` | Tạo công ty |
| PUT | `/companies` | Cập nhật công ty |
| DELETE | `/companies/{id}` | Xóa công ty |

### 💼 Job

| Method | Endpoint | Mô tả nhanh |
|---|---|---|
| GET | `/jobs` | Lấy danh sách việc làm |
| GET | `/jobs/{id}` | Lấy chi tiết việc làm |
| POST | `/jobs` | Tạo việc làm |
| PUT | `/jobs` | Cập nhật việc làm |
| DELETE | `/jobs/{id}` | Xóa việc làm |

### 🧠 Skill

| Method | Endpoint | Mô tả nhanh |
|---|---|---|
| GET | `/skills` | Lấy danh sách skill |
| POST | `/skills` | Tạo skill |
| PUT | `/skills` | Cập nhật skill |
| DELETE | `/skills/{id}` | Xóa skill |

### 🛡️ Role

| Method | Endpoint | Mô tả nhanh |
|---|---|---|
| GET | `/roles` | Lấy danh sách role |
| GET | `/roles/{id}` | Lấy chi tiết role |
| POST | `/roles` | Tạo role |
| PUT | `/roles` | Cập nhật role |
| DELETE | `/roles/{id}` | Xóa role |

### 🔐 Permission

| Method | Endpoint | Mô tả nhanh |
|---|---|---|
| GET | `/permissions` | Lấy danh sách permission |
| POST | `/permissions` | Tạo permission |
| PUT | `/permissions` | Cập nhật permission |
| DELETE | `/permissions/{id}` | Xóa permission |

### 📄 Resume

| Method | Endpoint | Mô tả nhanh |
|---|---|---|
| GET | `/resumes` | Lấy danh sách CV |
| GET | `/resumes/{id}` | Lấy chi tiết CV |
| POST | `/resumes` | Tạo CV |
| PUT | `/resumes` | Cập nhật CV |
| DELETE | `/resumes/{id}` | Xóa CV |
| POST | `/resumes/by-user` | Tạo CV theo user |

### 📁 File

| Method | Endpoint | Mô tả nhanh |
|---|---|---|
| POST | `/files` | Upload file |
| GET | `/files` | Download/View file |

### 🧪 Public test

| Method | Endpoint | Mô tả nhanh |
|---|---|---|
| GET | `/` | Kiểm tra API đang chạy |

## 3. Security note

Public theo config:

- `/`
- `/api/v1/auth/login`
- `/api/v1/auth/refresh`
- `/api/v1/auth/register`
- `GET /api/v1/companies/`
- `GET /api/v1/jobs/`
- `GET /api/v1/skills/**`

Các API còn lại yêu cầu JWT token.

## 4. Mẫu application.properties

Lưu ý: file `src/main/resources/application.properties` đang bị ignore trên Git.
Hãy tạo file local theo mẫu dưới đây:

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

## 5. Chạy nhanh dự án

### Bước 1: Tạo database MySQL

```sql
CREATE DATABASE zob CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### Bước 2: Tạo file cấu hình local

Tạo file `src/main/resources/application.properties` theo mẫu ở trên.

### Bước 3: Chạy ứng dụng

```bash
.\gradlew.bat bootRun
```

### Bước 4: Kiểm tra

```http
GET http://localhost:8080/
```

Kỳ vọng trả về: `Hello World`