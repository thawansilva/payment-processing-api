CREATE TABLE users (
    id UUID primary key,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(15) NOT NULL,
    enabled BOOLEAN NOT NULL default TRUE,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
)