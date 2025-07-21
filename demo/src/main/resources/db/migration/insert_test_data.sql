-- Insert test data for quizzes
INSERT INTO Quiz (title, difficulty_level) VALUES 
('Java Basics Quiz', 'BEGINNER'),
('Spring Boot Advanced', 'INTERMEDIATE'),
('Database Design', 'ADVANCED');

-- Insert test data for questions
INSERT INTO Question (question_text, difficulty_level) VALUES 
('What is Java?', 'EASY'),
('What is Spring Boot?', 'INTERMEDIATE'),
('What is a foreign key?', 'HARD');

-- Insert test data for answers
INSERT INTO Answer (answer_text, is_correct, question_id) VALUES 
('Java is a programming language', 1, 1),
('Java is a database', 0, 1),
('A framework for building applications', 1, 2),
('A text editor', 0, 2),
('A column that references another table', 1, 3),
('A primary key', 0, 3);

-- Insert test data for stories
INSERT INTO Story (title, content, category) VALUES 
('Learning Java', 'This is a story about learning Java programming...', 'EDUCATIONAL'),
('Spring Boot Journey', 'A developer''s experience with Spring Boot...', 'TECHNICAL');

-- Update any admin user
UPDATE Users SET role = 'ADMIN' WHERE id = 1;
