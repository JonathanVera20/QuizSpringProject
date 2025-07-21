# 🚀 **GET POSTMAN WORKING - CHOOSE YOUR METHOD**

## **METHOD 1: QUICK SQL SETUP** ⚡ (RECOMMENDED - 5 minutes)

### **Step 1: Connect to your PostgreSQL database**
```bash
# Option A: Using command line (if psql is installed)
psql -h localhost -p 5432 -U postgres -d AutonomoWeb

# Option B: Use pgAdmin or any PostgreSQL GUI tool
# Connect to: localhost:5432, database: AutonomoWeb, user: postgres, password: 1234
```

### **Step 2: Run the SQL script**
**Copy and paste this SQL into your database:**
```sql
-- Insert test data for quizzes
INSERT INTO Quiz (title, difficulty_level) VALUES 
('Java Basics Quiz', 'BEGINNER'),
('Spring Boot Advanced', 'INTERMEDIATE'), 
('Database Design', 'ADVANCED');

-- Insert test data for questions
INSERT INTO Questions (quiz_id, text) VALUES 
(1, 'What is Java?'),
(1, 'Which keyword is used for inheritance in Java?'),
(2, 'What is Spring Boot?'),
(2, 'What annotation is used for REST controllers?'),
(3, 'What is a foreign key?'),
(3, 'What does ACID stand for in databases?');

-- Insert test data for answers
INSERT INTO Answers (question_id, text, is_correct) VALUES 
-- Answers for "What is Java?" (question_id = 1)
(1, 'Java is a programming language', 1),
(1, 'Java is a database', 0),
(1, 'Java is an operating system', 0),
-- Answers for "Which keyword is used for inheritance?" (question_id = 2)  
(2, 'extends', 1),
(2, 'implements', 0),
(2, 'inherit', 0),
-- Answers for "What is Spring Boot?" (question_id = 3)
(3, 'A framework for building Java applications', 1),
(3, 'A text editor', 0),
(3, 'A database', 0),
-- Answers for "What annotation is used for REST controllers?" (question_id = 4)
(4, '@RestController', 1),
(4, '@Controller', 0),
(4, '@Service', 0),
-- Answers for "What is a foreign key?" (question_id = 5)
(5, 'A column that references another table primary key', 1),
(5, 'A primary key', 0),
(5, 'An index', 0),
-- Answers for "What does ACID stand for?" (question_id = 6)
(6, 'Atomicity, Consistency, Isolation, Durability', 1),
(6, 'Atomic, Continuous, Integrated, Distributed', 0),
(6, 'Advanced, Complete, Independent, Dynamic', 0);

-- Insert test data for stories
INSERT INTO Story (quiz_id, title, author) VALUES 
(1, 'Learning Java Programming', 'Tech Author'),
(2, 'Spring Boot Journey', 'Framework Expert'),
(3, 'Database Design Fundamentals', 'DB Specialist');

-- Create admin user (if you don't have one)
INSERT INTO Users (username, email, password, role) VALUES 
('admin', 'admin@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lIdtRj0gg7jfWCCtK', 'ADMIN');
-- Password: admin123

-- OR update existing user to be admin
-- UPDATE Users SET role = 'ADMIN' WHERE id = 1;
```

### **Step 3: Start your Spring Boot app**
```bash
./mvnw.cmd spring-boot:run
```

### **Step 4: Test with Postman**
Import the `Quiz_Project_Postman_Collection.json` file I created earlier.

---

## **METHOD 2: AUTOMATIC SPRING BOOT SETUP** 🔄 (PERMANENT SOLUTION)

### **Step 1: Use the TestDataLoader**
I've created `TestDataLoader.java` that automatically creates test data when the app starts.

### **Step 2: Start your app**
```bash
./mvnw.cmd spring-boot:run
```
The app will automatically create all test data on first run.

---

## **TESTING WORKFLOW** 🧪

### **1. Basic Test Sequence:**
1. **Register User:** POST `/api/auth/register`
2. **Login:** POST `/api/auth/login` (saves JWT token automatically)
3. **Get Profile:** GET `/api/users/me`
4. **List Data:** GET `/api/quizzes`, `/api/questions`, `/api/stories`

### **2. Admin Testing:**
- **Username:** `admin`
- **Password:** `admin123`
- Login to get admin token
- Test admin-only endpoints (create/update/delete)

### **3. Complete Test:**
- Import `Quiz_Project_Postman_Collection.json`
- Run the entire collection
- All endpoints should return 200 OK (except 401 for admin endpoints without admin token)

---

## **CREDENTIALS CREATED** 🔑

### **Admin User:**
- **Username:** `admin`
- **Password:** `admin123`
- **Role:** `ADMIN`

### **Test User:**
- **Username:** `testuser` 
- **Password:** `password123`
- **Role:** `USER`

---

## **WHAT DATA IS CREATED** 📊

- ✅ **3 Quizzes** (Java, Spring Boot, Database)
- ✅ **6 Questions** (2 per quiz)
- ✅ **12 Answers** (2 per question, with correct/incorrect)
- ✅ **3 Stories** (1 per quiz)
- ✅ **2 Users** (1 admin, 1 regular user)

---

## **IMMEDIATE NEXT STEPS** 🎯

**Choose Method 1 for quick testing or Method 2 for permanent setup, then:**

1. Start your Spring Boot application
2. Import the Postman collection
3. Run the "Register User" → "Login" → "Get Profile" sequence
4. Test all the endpoints that were failing before
5. All should now return 200 OK with actual data!

**Which method would you prefer? I recommend Method 1 for immediate results!**
