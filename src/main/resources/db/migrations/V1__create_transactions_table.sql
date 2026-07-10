CREATE TABLE transactions (
    id BIGSERIAL PRIMARY KEY,
    status VARCHAR(10) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
)