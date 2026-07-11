INSERT INTO accounts (owner_name, currency, created_at) VALUES
    ('Alice', 'INR', now()),
    ('Bob', 'INR', now());

INSERT INTO transactions (status, created_at) VALUES
    ('COMPLETED', now());

-- Alice pays Bob 100 — double-entry pair for the transaction above
INSERT INTO ledger_entry (transaction_id, account_id, direction, amount, currency, created_at) VALUES
    (1, 1, 'DEBIT', 100.00, 'INR', now()),
    (1, 2, 'CREDIT', 100.00, 'INR', now());