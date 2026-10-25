CREATE TABLE audit_log (
	id UUID PRIMARY KEY,
	actor_id UUID REFERENCES users(id) NOT NULL,
	transaction_id UUID REFERENCES transactions(id) NOT NULL,
	action VARCHAR(50) NOT NULL,
	details VARCHAR(500),
	created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_log_user_id ON audit_log(actor_id);
CREATE INDEX idx_audit_log_transaction_id ON audit_log(transaction_id);