CREATE TABLE refresh_tokens (
    id UUID primary key,
    user_id UUID REFERENCES users(id) NOT NULL,
    token VARCHAR(36) NOT NULL UNIQUE,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    expires_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);