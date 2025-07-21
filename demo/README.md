# Quiz Project - Database Setup Guide

## 🚀 Quick Start

### Prerequisites
- PostgreSQL 12+ installed
- Java 17+ installed
- Maven installed

### 1. Database Setup

```bash
# Connect to PostgreSQL
psql -U postgres -h localhost

# Create database
CREATE DATABASE AutonomoWeb;

# Exit and reconnect to the new database
\q
psql -U postgres -h localhost -d AutonomoWeb

# Run the schema setup
\i database_setup.sql

# Load test data
\i COMPLETE_TEST_DATA.sql
```

### 2. Application Setup

```bash
# Clone the repository
git clone [your-repo-url]
cd Quizproject/demo

# Set environment variables (optional)
export DATABASE_URL=jdbc:postgresql://localhost:5432/AutonomoWeb
export DATABASE_USERNAME=postgres
export DATABASE_PASSWORD=1234

# Build and run
mvn clean install
mvn spring-boot:run
```

### 3. Testing the API

The application will start on `http://localhost:8080`

**Test the setup:**
```bash
curl http://localhost:8080/api/test/public
```

**Import Postman Collection:**
- Import `Quiz_Project_Postman_Collection.json`
- Set environment variable `base_url` to `http://localhost:8080`

## 📊 Database Schema

### Tables Created:
- `users` - User accounts and authentication
- `story` - Story content for quizzes
- `quiz` - Quiz definitions
- `questions` - Quiz questions
- `answers` - Answer options for questions
- `quiz_attempt` - User quiz attempts
- `quiz_progress` - Progress tracking

### Default Admin User:
- Username: `admintest`
- Password: `password123`
- Email: `admintest@example.com`

## 🔐 API Authentication

1. **Register/Login** to get JWT token
2. **Admin operations** require ADMIN role
3. **User operations** are filtered by ownership

## 📁 Project Structure

```
demo/
├── src/main/java/com/example/demo/
│   ├── controller/     # REST API endpoints
│   ├── model/         # JPA entities
│   ├── repository/    # Data access layer
│   ├── service/       # Business logic
│   ├── security/      # JWT authentication
│   └── config/        # Configuration
├── database_setup.sql # Database schema
├── COMPLETE_TEST_DATA.sql # Sample data
└── Quiz_Project_Postman_Collection.json # API testing
```

## 🎯 Features

- ✅ JWT Authentication & Authorization
- ✅ Role-based Access Control (USER/ADMIN)
- ✅ Complete CRUD operations for all entities
- ✅ Secure endpoint access controls
- ✅ Comprehensive API documentation
- ✅ Database relationship management
- ✅ Quiz attempt and progress tracking
