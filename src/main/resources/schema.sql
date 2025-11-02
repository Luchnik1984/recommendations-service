-- Таблица банковских продуктов
CREATE TABLE IF NOT EXISTS bank_products (
                                             id UUID PRIMARY KEY,
                                             name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    category VARCHAR(100),
    min_amount DECIMAL(15,2),
    max_amount DECIMAL(15,2),
    interest_rate DECIMAL(5,2),
    description TEXT
    );

-- Таблица пользователей банка
CREATE TABLE IF NOT EXISTS bank_users (
                                          id UUID PRIMARY KEY,
                                          first_name VARCHAR(100),
    last_name VARCHAR(100),
    email VARCHAR(255),
    birth_date DATE,
    city VARCHAR(100)
    );

-- Таблица операций пользователей
CREATE TABLE IF NOT EXISTS user_operations (
                                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                               user_id UUID NOT NULL,
                                               product_id UUID NOT NULL,
                                               operation_type VARCHAR(50) NOT NULL,
    amount DECIMAL(15,2),
    operation_date TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES bank_users(id),
    FOREIGN KEY (product_id) REFERENCES bank_products(id)
    );