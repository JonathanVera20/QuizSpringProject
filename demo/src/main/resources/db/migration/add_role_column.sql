-- Add role column to Users table
ALTER TABLE Users ADD COLUMN role VARCHAR(20) DEFAULT 'USER';

-- Update existing users to have USER role by default
UPDATE Users SET role = 'USER' WHERE role IS NULL OR role = '';

-- Create an admin user (optional)
-- UPDATE Users SET role = 'ADMIN' WHERE username = 'admin';
