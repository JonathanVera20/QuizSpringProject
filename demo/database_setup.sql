-- ===============================================
-- QUIZ PROJECT - DATABASE SETUP SCRIPT
-- ===============================================
-- This script creates the complete database schema
-- for the Quiz Application Spring Boot project
-- ===============================================

-- Create Database (run this separately if needed)
-- CREATE DATABASE AutonomoWeb;
-- \c AutonomoWeb;

-- Drop existing tables if they exist (for clean setup)
DROP TABLE IF EXISTS quiz_progress CASCADE;
DROP TABLE IF EXISTS quiz_attempt CASCADE;
DROP TABLE IF EXISTS answers CASCADE;
DROP TABLE IF EXISTS questions CASCADE;
DROP TABLE IF EXISTS quiz CASCADE;
DROP TABLE IF EXISTS story CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- ===============================================
-- CREATE TABLES
-- ===============================================

-- Users table
CREATE TABLE users (
    user_id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) DEFAULT 'USER' CHECK (role IN ('USER', 'ADMIN'))
);

-- Story table
CREATE TABLE story (
    story_id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    imageurl VARCHAR(500)
);

-- Quiz table
CREATE TABLE quiz (
    quiz_id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    difficulty_level VARCHAR(50),
    story_id BIGINT,
    FOREIGN KEY (story_id) REFERENCES story(story_id) ON DELETE SET NULL
);

-- Questions table
CREATE TABLE questions (
    question_id BIGSERIAL PRIMARY KEY,
    content TEXT NOT NULL,
    quiz_id BIGINT NOT NULL,
    points INTEGER DEFAULT 0,
    FOREIGN KEY (quiz_id) REFERENCES quiz(quiz_id) ON DELETE CASCADE
);

-- Answers table
CREATE TABLE answers (
    answer_id BIGSERIAL PRIMARY KEY,
    content TEXT NOT NULL,
    is_correct BOOLEAN DEFAULT FALSE,
    question_id BIGINT NOT NULL,
    FOREIGN KEY (question_id) REFERENCES questions(question_id) ON DELETE CASCADE
);

-- Quiz Attempt table
CREATE TABLE quiz_attempt (
    attempt_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    quiz_id BIGINT NOT NULL,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    score INTEGER,
    id BIGINT NOT NULL DEFAULT nextval('quiz_attempt_attempt_id_seq'),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (quiz_id) REFERENCES quiz(quiz_id) ON DELETE CASCADE
);

-- Quiz Progress table
CREATE TABLE quiz_progress (
    progress_id BIGSERIAL PRIMARY KEY,
    attempt_id BIGINT,
    quiz_id BIGINT NOT NULL,
    completed INTEGER DEFAULT 0,
    score INTEGER DEFAULT 0,
    attempt_attempt_id BIGINT,
    FOREIGN KEY (attempt_id) REFERENCES quiz_attempt(attempt_id) ON DELETE CASCADE,
    FOREIGN KEY (quiz_id) REFERENCES quiz(quiz_id) ON DELETE CASCADE,
    FOREIGN KEY (attempt_attempt_id) REFERENCES quiz_attempt(attempt_id) ON DELETE CASCADE
);

-- ===============================================
-- CREATE INDEXES FOR PERFORMANCE
-- ===============================================

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_quiz_difficulty ON quiz(difficulty_level);
CREATE INDEX idx_questions_quiz_id ON questions(quiz_id);
CREATE INDEX idx_answers_question_id ON answers(question_id);
CREATE INDEX idx_quiz_attempt_user_id ON quiz_attempt(user_id);
CREATE INDEX idx_quiz_attempt_quiz_id ON quiz_attempt(quiz_id);
CREATE INDEX idx_quiz_progress_quiz_id ON quiz_progress(quiz_id);

-- ===============================================
-- SCRIPT COMPLETION
-- ===============================================

SELECT 'Database schema created successfully!' as message;
SELECT 'Tables created: ' || count(*) || ' tables' as summary 
FROM information_schema.tables 
WHERE table_schema = 'public' 
AND table_name IN ('users', 'story', 'quiz', 'questions', 'answers', 'quiz_attempt', 'quiz_progress');
