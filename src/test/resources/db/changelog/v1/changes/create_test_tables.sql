-- ==========================================================================
-- LIQUIBASE MIGRATION: СОЗДАНИЕ ТЕСТОВОЙ СТРУКТУРЫ H2 БАЗЫ
-- ТОЧНАЯ КОПИЯ ПРОДАКШЕН СТРУКТУРЫ БАЗЫ TRANSACTION
-- ==========================================================================

-- Changeset: create_users_table
-- Comment: Создание таблицы пользователей (точная копия продакшн)

CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    first_name VARCHAR(255),
    last_name VARCHAR(255)
    );

COMMENT ON TABLE users IS 'Таблица пользователей (точная копия продакшн)';

-- Changeset: create_products_table
-- Comment: Создание таблицы продуктов (точная копия продакшн)

CREATE TABLE IF NOT EXISTS products (
    id UUID PRIMARY KEY,
    type VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL
    );

COMMENT ON TABLE products IS 'Таблица банковских продуктов (точная копия продакшн)';

-- Changeset: create_transactions_table
-- Comment: Создание таблицы транзакций (точная копия продакшн)

CREATE TABLE IF NOT EXISTS transactions (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL,
    user_id UUID NOT NULL,
    type VARCHAR(255) NOT NULL CHECK (type IN ('DEPOSIT', 'WITHDRAW')),
    amount INTEGER NOT NULL CHECK (amount >= 0),  -- INTEGER как в продакшн
    CONSTRAINT fk_transactions_product_id FOREIGN KEY (product_id) REFERENCES products(id)
    );

COMMENT ON TABLE transactions IS 'Таблица финансовых транзакций (точная копия продакшн)';

-- ==========================================================================
-- СОЗДАНИЕ ИНДЕКСОВ (ТОЧНАЯ КОПИЯ ПРОДАКШЕН ИНДЕКСОВ)
-- ==========================================================================

-- Changeset: create_transaction_indexes
-- Comment: Создание индексов для оптимизации запросов

CREATE INDEX IF NOT EXISTS idx_transactions_user_id ON transactions(user_id);
CREATE INDEX IF NOT EXISTS idx_transactions_product_id ON transactions(product_id);
CREATE INDEX IF NOT EXISTS idx_transactions_type ON transactions(type);

-- Changeset: create_products_indexes
-- Comment: Создание индексов для таблицы продуктов

CREATE INDEX IF NOT EXISTS idx_products_type ON products(type);

-- Changeset: create_users_indexes
-- Comment: Создание индексов для таблицы пользователей

CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);