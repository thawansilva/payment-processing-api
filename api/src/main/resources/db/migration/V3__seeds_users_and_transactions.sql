INSERT INTO users (id, username, password_hash, role)
VALUES ('11111111-1111-1111-1111-111111111111', 'user1',
        '$2b$10$yHQR3CxIqik3vUCBdQwm8.mq5GxsroMN7X7oF.RFG5xWuOv3.vt9S', 'user');

INSERT INTO transactions (id, account_id, amount, currency, status, created_at) VALUES
       ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111', 150.0000, 'EUR', 'completed', now() - interval '2 days'),
       ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '11111111-1111-1111-1111-111111111111', 42.5000,  'EUR', 'pending',   now() - interval '1 day'),
       ('cccccccc-cccc-cccc-cccc-cccccccccccc',  '11111111-1111-1111-1111-111111111111', 999.9900, 'USD', 'failed',    now());