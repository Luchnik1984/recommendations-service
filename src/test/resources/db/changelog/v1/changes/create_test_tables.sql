CREATE TABLE IF NOT EXISTS users (
                                     id UUID PRIMARY KEY,
                                     username VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    email VARCHAR(255),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS products (
                                        id UUID PRIMARY KEY,
                                        type VARCHAR(50) NOT NULL CHECK (type IN ('DEBIT', 'CREDIT', 'SAVING', 'INVEST')),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS transactions (
                                            id UUID PRIMARY KEY,
                                            product_id UUID NOT NULL,
                                            user_id UUID NOT NULL,
                                            type VARCHAR(50) NOT NULL CHECK (type IN ('DEPOSIT', 'WITHDRAW')),
    amount DECIMAL(15,2) NOT NULL CHECK (amount >= 0),
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_transactions_product_id FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_transactions_user_id FOREIGN KEY (user_id) REFERENCES users(id)
    );

-- Индексы (ТОЧНАЯ КОПИЯ ИЗ ПРОДАКШЕНА)
CREATE INDEX IF NOT EXISTS idx_transactions_user_id ON transactions(user_id);
CREATE INDEX IF NOT EXISTS idx_transactions_product_id ON transactions(product_id);
CREATE INDEX IF NOT EXISTS idx_transactions_type ON transactions(type);
CREATE INDEX IF NOT EXISTS idx_products_type ON products(type);
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);