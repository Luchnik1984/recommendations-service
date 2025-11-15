CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(255),
    last_name VARCHAR(255));

CREATE TABLE IF NOT EXISTS products (
    id UUID PRIMARY KEY,
    type VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255));

CREATE TABLE IF NOT EXISTS transactions (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL,
    user_id UUID NOT NULL,
    type VARCHAR(255) NOT NULL,
    amount INTEGER NOT NULL,
        CONSTRAINT fk_transactions_product_id
            FOREIGN KEY (product_id) REFERENCES products(id),
        CONSTRAINT fk_transactions_user_id
            FOREIGN KEY (user_id) REFERENCES users(id));