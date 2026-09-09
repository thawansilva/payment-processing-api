CREATE TYPE transaction_status AS ENUM ('pending', 'completed', 'failed', 'refunded');

CREATE TABLE transactions (
    id UUID primary key,
    account_id UUID REFERENCES users(id) NOT NULL,
    amount NUMERIC(19,4) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    status transaction_status NOT NULL default 'pending',
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT check_positive_amount CHECK (amount > 0)
);

CREATE INDEX idx_transactions_account_id ON transactions(account_id);