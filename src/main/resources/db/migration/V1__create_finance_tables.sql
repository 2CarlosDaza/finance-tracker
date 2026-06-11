CREATE TABLE categories (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    type        VARCHAR(10) NOT NULL,  -- INCOME or EXPENSE
    created_at  TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE transactions (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    category_id     UUID NOT NULL REFERENCES categories(id),
    type            VARCHAR(10) NOT NULL,  -- INCOME or EXPENSE
    amount          NUMERIC(15, 2) NOT NULL,
    currency        VARCHAR(3) NOT NULL DEFAULT 'USD',
    description     VARCHAR(255),
    transaction_date DATE NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_transactions_category_id      ON transactions(category_id);
CREATE INDEX idx_transactions_date             ON transactions(transaction_date);
CREATE INDEX idx_transactions_type             ON transactions(type);
CREATE INDEX idx_transactions_date_type        ON transactions(transaction_date, type);
