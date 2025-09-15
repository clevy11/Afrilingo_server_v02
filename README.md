# AfriLingo Backend Documentation

## Table of Contents

1. [Introduction](#introduction)
2. [System Requirements](#system-requirements)
3. [Installation and Setup](#installation-and-setup)
4. [Configuration](#configuration)
5. [Architecture Overview](#architecture-overview)
6. [Key Modules](#key-modules)
    - [Authentication and User Management](#authentication-and-user-management)
    - [Language Module](#language-module)
    - [Course Module](#course-module)
    - [Lesson Module](#lesson-module)
    - [Quiz Module](#quiz-module)
    - [User Progress Module](#user-progress-module)
    - [User Profile Module](#user-profile-module)
    - [Data Loader Module](#data-loader-module)
7. [API Endpoints Reference](#api-endpoints-reference)
8. [Integration Guide for Frontend Developers](#integration-guide-for-frontend-developers)
9. [Authentication Flow](#authentication-flow)
10. [Error Handling](#error-handling)
11. [Sample Requests and Responses](#sample-requests-and-responses)
12. [Best Practices](#best-practices)
13. [Troubleshooting](#troubleshooting)

## Introduction

AfriLingo is an educational platform designed to facilitate learning African languages. The backend system is built using Spring Boot and provides a comprehensive API for managing users, courses, lessons, quizzes, and tracking user progress.

The system currently supports learning Kinyarwanda, Kiswahili, and English with African contexts. It features a robust content management system, user authentication, progress tracking, and interactive quizzes.

## System Requirements

- Java 17 or higher
- PostgreSQL 13 or higher
- Maven 3.6+ or the included Maven wrapper (mvnw)
- 2GB RAM minimum (4GB recommended)
- 20GB disk space

## Installation and Setup

### Installing with Maven

1. Clone the repository:
   ```bash
   git clone https://github.com/your-org/afrilingo.git
   cd afrilingo
   ```

2. Build the project using Maven:
   ```bash
   ./mvnw clean install
   ```

   On Windows:
   ```bash
   mvnw.cmd clean install
   ```

3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

   On Windows:
   ```bash
   mvnw.cmd spring-boot:run
   ```

### Database Setup

1. Create a PostgreSQL database:
   ```sql
   CREATE DATABASE afrilingo_db;
   ```

2. The application will automatically create the necessary tables on first run due to `spring.jpa.hibernate.ddl-auto=update` configuration.

## Configuration

The application uses `application.properties` for configuration settings. Key configuration parameters include:

### Database Configuration
```properties
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.url=jdbc:postgresql://localhost:5432/afrilingo_db
spring.datasource.username=postgres
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
```

### JWT Authentication Configuration
```properties
application.security.jwt.secret-key=your_secret_key
application.security.jwt.expiration=86400000
application.security.jwt.refresh-token.expiration=604800000
```

### Swagger Documentation Configuration
```properties
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.operationsSorter=method
springdoc.swagger-ui.disable-swagger-default-url=true
springdoc.packagesToScan=edtech.afrilingo
springdoc.pathsToMatch=/api/**
```

## Architecture Overview

AfriLingo backend follows a layered architecture:

1. **Controller Layer** - Handles HTTP requests and responses
2. **Service Layer** - Contains business logic
3. **Repository Layer** - Manages data persistence
4. **Entity Layer** - Defines the data model

The application uses Spring Security with JWT for authentication and authorization, and Spring Data JPA for database interactions.

## Key Modules

### Authentication and User Management

The authentication module manages user registration, login, and token management. It uses JWT (JSON Web Tokens) for authentication.

Key components:
- `AuthenticationController` - Handles login, registration, and token refresh requests
- `JwtService` - Manages JWT token generation and validation
- `User` entity - Stores user information
- `TokenRepository` - Manages access tokens

### Language Module

This module manages the languages available for learning on the platform.

Key components:
- `Language` entity - Represents a language with properties like name, code, description
- `LanguageService` - Business logic for language operations
- `LanguageController` - API endpoints for language operations

### Course Module

The course module manages course creation, retrieval, and updates.

Key components:
- `Course` entity - Represents a course with properties like title, description, level
- `CourseService` - Business logic for course operations
- `CourseController` - API endpoints for course operations

### Lesson Module

The lesson module manages lessons within courses and their content.

Key components:
- `Lesson` entity - Represents a lesson with properties like title, description, type
- `LessonContent` entity - Stores the actual content of lessons
- `LessonService` - Business logic for lesson operations
- `LessonController` - API endpoints for lesson operations

### Quiz Module

The quiz module manages quizzes, questions, and options for assessments.

Key components:
- `Quiz` entity - Represents a quiz with properties like title, description, minimum passing score
- `Question` entity - Represents a question within a quiz
- `Option` entity - Represents answer options for a question
- `QuizService` - Business logic for quiz operations
- `QuestionService` - Business logic for question operations
- `QuizController` - API endpoints for quiz operations

### User Progress Module

This module tracks user progress through courses, lessons, and quizzes.

Key components:
- `UserProgress` entity - Tracks lesson completion
- `UserQuizAttempt` entity - Tracks quiz attempts
- `UserAnswer` entity - Stores user answers to quiz questions
- `UserProgressService` - Business logic for tracking progress
- `UserDashboardService` - Aggregates user progress data for dashboards

### User Profile Module

The user profile module manages user preferences and learning settings.

Key components:
- `UserProfile` entity - Stores user profile information
- `UserProfileService` - Business logic for profile operations
- `UserProfileController` - API endpoints for profile operations

### Data Loader Module

The data loader module provides functionality to initialize the system with predefined data for testing or production use.

Key components:
- `DataLoaderService` - Service for loading sample data
- `DataLoaderController` - Admin API for triggering data loading
- `LanguageContentHelper` - Helper class with language-specific content

## API Endpoints Reference

### Authentication Endpoints

- `POST /api/v1/auth/register` - Register a new user
- `POST /api/v1/auth/authenticate` - Login and get JWT token
- `POST /api/v1/auth/refresh-token` - Refresh JWT token

### Language Endpoints

- `GET /api/v1/languages` - Get all languages
- `GET /api/v1/languages/{id}` - Get language by ID
- `GET /api/v1/languages/code/{code}` - Get language by code
- `POST /api/v1/languages` - Create a new language
- `PUT /api/v1/languages/{id}` - Update a language
- `DELETE /api/v1/languages/{id}` - Delete a language

### Course Endpoints

- `GET /api/v1/courses` - Get all courses
- `GET /api/v1/courses/{id}` - Get course by ID
- `GET /api/v1/courses/language/{languageId}` - Get courses by language ID
- `GET /api/v1/courses/language/{languageId}/active` - Get active courses by language ID
- `POST /api/v1/courses` - Create a new course
- `PUT /api/v1/courses/{id}` - Update a course
- `PATCH /api/v1/courses/{id}/activation` - Set course activation status
- `DELETE /api/v1/courses/{id}` - Delete a course

### Lesson Endpoints

- `GET /api/v1/lessons` - Get all lessons
- `GET /api/v1/lessons/{id}` - Get lesson by ID
- `GET /api/v1/lessons/course/{courseId}` - Get lessons by course ID
- `GET /api/v1/lessons/course/{courseId}/ordered` - Get ordered lessons by course ID
- `GET /api/v1/lessons/type/{lessonType}` - Get lessons by type
- `POST /api/v1/lessons` - Create a new lesson
- `PUT /api/v1/lessons/{id}` - Update a lesson
- `DELETE /api/v1/lessons/{id}` - Delete a lesson
- `POST /api/v1/lessons/course/{courseId}/reorder` - Reorder lessons

### Quiz Endpoints

- `GET /api/v1/quizzes` - Get all quizzes
- `GET /api/v1/quizzes/{id}` - Get quiz by ID
- `GET /api/v1/quizzes/lesson/{lessonId}` - Get quizzes by lesson ID
- `POST /api/v1/quizzes` - Create a new quiz
- `PUT /api/v1/quizzes/{id}` - Update a quiz
- `DELETE /api/v1/quizzes/{id}` - Delete a quiz
- `GET /api/v1/quizzes/{id}/questions` - Get questions for a quiz
- `POST /api/v1/quizzes/{id}/questions` - Add a question to a quiz
- `GET /api/v1/quizzes/{id}/statistics` - Get quiz statistics

### Question Endpoints

- `GET /api/v1/questions` - Get all questions
- `GET /api/v1/questions/{id}` - Get question by ID
- `GET /api/v1/questions/{id}/options` - Get options for a question
- `POST /api/v1/questions` - Create a new question
- `PUT /api/v1/questions/{id}` - Update a question
- `DELETE /api/v1/questions/{id}` - Delete a question
- `POST /api/v1/questions/{id}/options` - Add an option to a question

### User Progress Endpoints

- `GET /api/v1/quiz-attempts` - Get all quiz attempts for current user
- `GET /api/v1/quiz-attempts/{id}` - Get quiz attempt by ID
- `POST /api/v1/quiz-attempts/quiz/{quizId}` - Create a new quiz attempt
- `GET /api/v1/quiz-attempts/{id}/answers` - Get answers for a quiz attempt
- `GET /api/v1/quiz-attempts/statistics` - Get user quiz statistics

### User Profile Endpoints

- `GET /api/v1/profile` - Get current user's profile
- `GET /api/v1/profile/exists` - Check if user has a profile
- `POST /api/v1/profile` - Create or update user profile
- `PUT /api/v1/profile/languages` - Update languages to learn
- `PUT /api/v1/profile/preferences` - Update learning preferences
- `PUT /api/v1/profile/picture` - Update profile picture

### Dashboard Endpoints

- `GET /api/v1/dashboard` - Get user dashboard data

### Admin Data Loader Endpoints

- `POST /api/admin/data-loader/load-all` - Load all sample data
- `POST /api/admin/data-loader/load-languages` - Load language data
- `POST /api/admin/data-loader/load-courses` - Load course data
- `POST /api/admin/data-loader/load-lessons` - Load lesson data
- `POST /api/admin/data-loader/load-lesson-content` - Load lesson content
- `POST /api/admin/data-loader/load-quizzes` - Load quiz data
- `DELETE /api/admin/data-loader/reset` - Reset all data

## Integration Guide for Frontend Developers

### Getting Started

1. **API Base URL**: The base URL for all API requests is:
    - Development: `http://localhost:8080/api/v1`
    - Production: `https://api.afrilingo.com/api/v1`

2. **Authentication**: All API requests (except authentication endpoints) require JWT authentication.

3. **Request Headers**:
   ```
   Authorization: Bearer {jwt_token}
   Content-Type: application/json
   ```

### Working with the Authentication Flow

1. Register a user:
   ```javascript
   fetch('/api/v1/auth/register', {
     method: 'POST',
     headers: { 'Content-Type': 'application/json' },
     body: JSON.stringify({
       firstname: 'John',
       lastname: 'Doe',
       email: 'john@example.com',
       password: 'password123',
       role: 'ROLE_USER'
     })
   });
   ```

2. Authenticate a user:
   ```javascript
   fetch('/api/v1/auth/authenticate', {
     method: 'POST',
     headers: { 'Content-Type': 'application/json' },
     body: JSON.stringify({
       email: 'john@example.com',
       password: 'password123'
     })
   })
   .then(response => response.json())
   .then(data => {
     // Store tokens in local storage or secure cookie
     localStorage.setItem('access_token', data.access_token);
     localStorage.setItem('refresh_token', data.refresh_token);
   });
   ```

3. Make authenticated requests:
   ```javascript
   fetch('/api/v1/profile', {
     headers: {
       'Authorization': `Bearer ${localStorage.getItem('access_token')}`,
       'Content-Type': 'application/json'
     }
   });
   ```

4. Refresh token:
   ```javascript
   fetch('/api/v1/auth/refresh-token', {
     method: 'POST',
     headers: {
       'Authorization': `Bearer ${localStorage.getItem('refresh_token')}`
     }
   })
   .then(response => response.json())
   .then(data => {
     localStorage.setItem('access_token', data.access_token);
   });
   ```

### Working with Languages and Courses

1. Get all languages:
   ```javascript
   fetch('/api/v1/languages', {
     headers: {
       'Authorization': `Bearer ${localStorage.getItem('access_token')}`
     }
   })
   .then(response => response.json())
   .then(data => {
     // Process languages data
     const languages = data.data;
   });
   ```

2. Get courses for a language:
   ```javascript
   fetch(`/api/v1/courses/language/${languageId}`, {
     headers: {
       'Authorization': `Bearer ${localStorage.getItem('access_token')}`
     }
   })
   .then(response => response.json())
   .then(data => {
     // Process courses data
   });
   ```

### Working with Lessons and Content

1. Get lessons for a course:
   ```javascript
   fetch(`/api/v1/lessons/course/${courseId}/ordered`, {
     headers: {
       'Authorization': `Bearer ${localStorage.getItem('access_token')}`
     }
   })
   .then(response => response.json())
   .then(data => {
     // Process lessons data
   });
   ```

### Working with Quizzes

1. Get quizzes for a lesson:
   ```javascript
   fetch(`/api/v1/quizzes/lesson/${lessonId}`, {
     headers: {
       'Authorization': `Bearer ${localStorage.getItem('access_token')}`
     }
   })
   .then(response => response.json())
   .then(data => {
     // Process quizzes data
   });
   ```

2. Submit a quiz attempt:
   ```javascript
   fetch(`/api/v1/quiz-attempts/quiz/${quizId}`, {
     method: 'POST',
     headers: {
       'Authorization': `Bearer ${localStorage.getItem('access_token')}`,
       'Content-Type': 'application/json'
     },
     body: JSON.stringify({
       // Map of question IDs to selected option IDs
       "1": 3,
       "2": 7,
       "3": 12
     })
   })
   .then(response => response.json())
   .then(data => {
     // Process quiz attempt results
   });
   ```

### Working with User Profiles

1. Create or update a user profile:
   ```javascript
   fetch('/api/v1/profile', {
     method: 'POST',
     headers: {
       'Authorization': `Bearer ${localStorage.getItem('access_token')}`,
       'Content-Type': 'application/json'
     },
     body: JSON.stringify({
       country: 'United States',
       firstLanguage: 'English',
       reasonToLearn: 'Travel',
       languagesToLearnIds: [1, 2]
     })
   });
   ```

2. Get user dashboard data:
   ```javascript
   fetch('/api/v1/dashboard', {
     headers: {
       'Authorization': `Bearer ${localStorage.getItem('access_token')}`
     }
   })
   .then(response => response.json())
   .then(data => {
     // Process dashboard data
   });
   ```

## Authentication Flow

1. **Registration**: User registers with email and password
2. **Login**: User logs in and receives JWT access and refresh tokens
3. **Using Access Token**: Use access token for API requests
4. **Token Refresh**: Use refresh token to get a new access token when it expires
5. **Logout**: Revoke tokens to log out

Token lifespan:
- Access token: 24 hours (86400000 ms)
- Refresh token: 7 days (604800000 ms)

## Error Handling

The API returns standardized error responses in the following format:

```json
{
  "timestamp": "2025-04-27T10:25:30.123Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid input",
  "path": "/api/v1/resource"
}
```

Common HTTP status codes:
- `200` - Success
- `201` - Created
- `400` - Bad Request (client error)
- `401` - Unauthorized (authentication required)
- `403` - Forbidden (permission denied)
- `404` - Not Found
- `500` - Internal Server Error

## Sample Requests and Responses

### Sample Request: Create a Course

```http
POST /api/v1/courses HTTP/1.1
Host: localhost:8080
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "title": "Beginner Kinyarwanda",
  "description": "Learn the basics of Kinyarwanda",
  "level": "Beginner",
  "image": "kinyarwanda-beginner.jpg",
  "isActive": true,
  "language": {
    "id": 1
  }
}
```

### Sample Response: Create a Course

```http
HTTP/1.1 201 Created
Content-Type: application/json

{
  "id": 10,
  "title": "Beginner Kinyarwanda",
  "description": "Learn the basics of Kinyarwanda",
  "level": "Beginner",
  "image": "kinyarwanda-beginner.jpg",
  "isActive": true,
  "language": {
    "id": 1,
    "name": "Kinyarwanda",
    "code": "RW",
    "description": "Kinyarwanda is the official language of Rwanda",
    "flagImage": "rwanda-flag.png"
  }
}
```

### Sample Request: Get User Dashboard

```http
GET /api/v1/dashboard HTTP/1.1
Host: localhost:8080
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Sample Response: Get User Dashboard

```http
HTTP/1.1 200 OK
Content-Type: application/json
~~~~
{
  "timestamp": "2025-04-27T10:30:15.123Z",
  "status": 200,
  "message": "Success",
  "data": {
    "userProfile": {
      "id": 1,
      "country": "United States",
      "firstLanguage": "English",
      "profilePicture": "john-avatar.jpg",
      "reasonToLearn": "Travel",
      "languagesToLearn": [
        {
          "id": 1,
          "name": "Kinyarwanda",
          "code": "RW"
        }
      ],
      "dailyReminders": true,
      "dailyGoalMinutes": 15,
      "preferredLearningTime": "Evening (5PM-8PM)"
    },
    "learningStats": {
      "completedLessons": 5,
      "averageQuizScore": 78.5,
      "streak": 3,
      "totalLearningMinutes": 75,
      "passRate": 80.0
    },
    "courseProgress": {
      "1": 45.0,
      "2": 20.0
    }
  }
}
```

## Best Practices

1. **Token Management**:
    - Store tokens securely (HttpOnly cookies preferred)
    - Implement token refresh mechanism
    - Handle expired tokens gracefully

2. **Error Handling**:
    - Implement global error handling
    - Display user-friendly error messages
    - Log detailed errors for debugging

3. **Loading States**:
    - Show loading indicators during API calls
    - Implement skeleton loaders for better UX

4. **Caching**:
    - Cache appropriate responses to improve performance
    - Implement proper cache invalidation

5. **Offline Support**:
    - Consider implementing offline functionality for lesson content
    - Queue quiz submissions if offline

## Troubleshooting

### Common Issues and Solutions

1. **Authentication Issues**:
    - Ensure you're sending the token in the correct format: `Bearer {token}`
    - Check if the token is expired
    - Verify user credentials

2. **403 Forbidden Errors**:
    - Ensure the user has the correct role for the action
    - Check if the user has permission to access the resource

3. **Data Loading Issues**:
    - For admin users, use the data loader endpoints to initialize the system
    - Check database connection settings

4. **Performance Issues**:
    - Use pagination for large result sets
    - Implement caching where appropriate
    - Monitor API response times

### Support Contacts

For backend support, please contact:
- Email: backend-support@afrilingo.com
- Developer Slack channel: #afrilingo-backend-support


## Docker

This project includes a multi-stage Dockerfile to build and run the Afrilingo Spring Boot application (Java 21).

### Build the image

```bash
# From the project root
docker build -t afrilingo:latest .
```

### Run the container

```bash
# Basic run (uses defaults from application.properties)
docker run --name afrilingo -p 8081:8081 afrilingo:latest

# Run with environment overrides (recommended)
docker run \
  --name afrilingo \
  -p 8081:8081 \
  -e JAVA_OPTS="-Xms256m -Xmx512m" \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e SPRING_DATASOURCE_URL="jdbc:postgresql://<host>:5432/afrilingo_db" \
  -e SPRING_DATASOURCE_USERNAME="afrilingo_db_user" \
  -e SPRING_DATASOURCE_PASSWORD="<password>" \
  afrilingo:latest
```

Notes:
- The application listens on port 8081 inside the container; map this to your host as needed (e.g., -p 8081:8081).
- You can pass any Spring property via environment variables using Spring Boot's relaxed binding. For example, `spring.datasource.url` becomes `SPRING_DATASOURCE_URL`.
- For JVM tuning, set `JAVA_OPTS` (e.g., `-Xms256m -Xmx512m`).

### Multi-container (PostgreSQL + App) example with Docker Compose

Create a simple docker-compose.yml (example):

```yaml
services:
  db:
    image: postgres:16
    environment:
      POSTGRES_DB: afrilingo_db
      POSTGRES_USER: afrilingo_db_user
      POSTGRES_PASSWORD: yourpassword
    ports:
      - "5432:5432"
    volumes:
      - db_data:/var/lib/postgresql/data

  app:
    image: afrilingo:latest
    depends_on:
      - db
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/afrilingo_db
      SPRING_DATASOURCE_USERNAME: afrilingo_db_user
      SPRING_DATASOURCE_PASSWORD: yourpassword
      SPRING_PROFILES_ACTIVE: prod
      JAVA_OPTS: -Xms256m -Xmx512m
    ports:
      - "8081:8081"

volumes:
  db_data:
```
