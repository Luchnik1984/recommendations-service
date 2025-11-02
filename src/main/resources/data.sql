-- Очистка существующих данных
DELETE FROM user_operations;
DELETE FROM bank_products;
DELETE FROM bank_users;

-- Добавление банковских продуктов
INSERT INTO bank_products (id, name, type, category, description) VALUES
-- DEBIT продукты (дебетовые карты, счета)
('11111111-1111-1111-1111-111111111111', 'Дебетовая карта Classic', 'DEBIT', 'CARD', 'Базовая дебетовая карта'),
('22222222-2222-2222-2222-222222222222', 'Дебетовая карта Gold', 'DEBIT', 'CARD', 'Премиальная дебетовая карта'),
('33333333-3333-3333-3333-333333333333', 'Текущий счет', 'DEBIT', 'ACCOUNT', 'Основной текущий счет'),

-- SAVING продукты
('44444444-4444-4444-4444-444444444444', 'Накопительный счет', 'SAVING', 'SAVINGS', 'Накопительный счет с процентами'),
('55555555-5555-5555-5555-555555555555', 'Сберегательный вклад', 'SAVING', 'DEPOSIT', 'Срочный сберегательный вклад'),

-- INVEST продукты
('147f6a0f-3b91-413b-ab99-87f081d60d5a', 'Invest 500', 'INVEST', 'INVESTMENT', 'Описание Invest 500'),

-- CREDIT продукты
('66666666-6666-6666-6666-666666666666', 'Кредитная карта', 'CREDIT', 'CARD', 'Кредитная карта с лимитом'),
('ab138afb-f3ba-4a93-b74f-0fcee86d447f', 'Простой кредит', 'CREDIT', 'LOAN', 'Описание Простой кредит'),

-- Дополнительные продукты для Top Saving
('59efc529-2fff-41af-baff-90ccd7402925', 'Top Saving', 'SAVING', 'SAVINGS', 'Описание Top Saving');

-- Добавление тестовых пользователей (из ТЗ)
INSERT INTO bank_users (id, first_name, last_name, email) VALUES
                                                              ('cd515076-5d8a-44be-930e-8d4fcb79f42d', 'Иван', 'Тестовый', 'ivan.test@example.com'),
                                                              ('d4a4d619-9a0c-4fc5-b0cb-76c49409546b', 'Петр', 'Примеров', 'petr.example@example.com'),
                                                              ('1f9b149c-6577-448a-bc94-16bea229b71a', 'Мария', 'Образцова', 'maria.sample@example.com');

-- Добавление операций для тестовых пользователей

-- Для пользователя cd515076-5d8a-44be-930e-8d4fcb79f42d (должен получить Invest 500)
-- У него есть DEBIT продукт, нет INVEST продуктов, SAVING пополнения > 1000
INSERT INTO user_operations (user_id, product_id, operation_type, amount, operation_date) VALUES
                                                                                              ('cd515076-5d8a-44be-930e-8d4fcb79f42d', '11111111-1111-1111-1111-111111111111', 'DEPOSIT', 500.00, CURRENT_TIMESTAMP),  -- DEBIT
                                                                                              ('cd515076-5d8a-44be-930e-8d4fcb79f42d', '44444444-4444-4444-4444-444444444444', 'DEPOSIT', 1500.00, CURRENT_TIMESTAMP); -- SAVING > 1000

-- Для пользователя d4a4d619-9a0c-4fc5-b0cb-76c49409546b (должен получить Top Saving)
-- У него есть DEBIT продукт, DEBIT пополнения >= 50000, DEBIT пополнения > DEBIT трат
INSERT INTO user_operations (user_id, product_id, operation_type, amount, operation_date) VALUES
                                                                                              ('d4a4d619-9a0c-4fc5-b0cb-76c49409546b', '22222222-2222-2222-2222-222222222222', 'DEPOSIT', 60000.00, CURRENT_TIMESTAMP),  -- DEBIT >= 50000
                                                                                              ('d4a4d619-9a0c-4fc5-b0cb-76c49409546b', '22222222-2222-2222-2222-222222222222', 'SPEND', 40000.00, CURRENT_TIMESTAMP);     -- DEBIT траты < пополнений

-- Для пользователя 1f9b149c-6577-448a-bc94-16bea229b71a (должен получить Простой кредит)
-- Нет CREDIT продуктов, DEBIT пополнения > DEBIT трат, DEBIT траты > 100000
INSERT INTO user_operations (user_id, product_id, operation_type, amount, operation_date) VALUES
                                                                                              ('1f9b149c-6577-448a-bc94-16bea229b71a', '33333333-3333-3333-3333-333333333333', 'DEPOSIT', 200000.00, CURRENT_TIMESTAMP),  -- DEBIT пополнения
                                                                                              ('1f9b149c-6577-448a-bc94-16bea229b71a', '33333333-3333-3333-3333-333333333333', 'SPEND', 150000.00, CURRENT_TIMESTAMP);    -- DEBIT траты > 100000