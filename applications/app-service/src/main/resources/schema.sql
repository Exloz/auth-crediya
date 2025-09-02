-- Schema creation script for users table
-- This script will be automatically executed by Spring Boot on application startup

CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    lastName VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    id_document VARCHAR(50) UNIQUE NOT NULL,
    phone_number VARCHAR(20),
    address TEXT,
    birth_date DATE,
    role_id VARCHAR(50),
    base_salary DECIMAL(15,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_id_document ON users(id_document);
CREATE INDEX IF NOT EXISTS idx_users_role_id ON users(role_id);

-- Credentials table for secure password storage
CREATE TABLE IF NOT EXISTS credentials (
    user_id BIGINT PRIMARY KEY REFERENCES users(user_id) ON DELETE CASCADE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create index for credentials lookup
CREATE INDEX IF NOT EXISTS idx_credentials_user_id ON credentials(user_id);
