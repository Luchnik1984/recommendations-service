
DELETE FROM transactions;
DELETE FROM products;
DELETE FROM users;

-- 2. Сначала вставляем пользователей
INSERT INTO users (id, username, first_name, last_name, email) VALUES
                                                                   ('00000000-0000-0000-0000-000000000001', 'invest.user', 'Алексей', 'Инвесторов', 'invest@test.ru'),
                                                                   ('00000000-0000-0000-0000-000000000002', 'saving.user', 'Мария', 'Сберегаева', 'saving@test.ru'),
                                                                   ('00000000-0000-0000-0000-000000000003', 'credit.user', 'Дмитрий', 'Кредитов', 'credit@test.ru'),
                                                                   ('00000000-0000-0000-0000-000000000004', 'basic.user', 'Ольга', 'Базовая', 'basic@test.ru'),
                                                                   ('00000000-0000-0000-0000-000000000005', 'active.user', 'Иван', 'Активный', 'active@test.ru');

-- 3. Затем вставляем продукты
INSERT INTO products (id, type, name, description) VALUES
                                                       ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'DEBIT', 'Дебетовая карта', 'Основная дебетовая карта'),
                                                       ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'CREDIT', 'Кредитная карта', 'Кредитная карта с льготным периодом'),
                                                       ('cccccccc-cccc-cccc-cccc-cccccccccccc', 'SAVING', 'Накопительный счет', 'Счет для накоплений'),
                                                       ('dddddddd-dddd-dddd-dddd-dddddddddddd', 'INVEST', 'Инвестиционный счет', 'Счет для инвестиций');

-- 4. И только потом транзакции (они зависят от пользователей и продуктов)
INSERT INTO transactions (id, product_id, user_id, type, amount) VALUES
                                                                     -- Транзакции для пользователя 1 (invest.user)
                                                                     ('11111111-1111-1111-1111-111111111111', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '00000000-0000-0000-0000-000000000001', 'DEPOSIT', 50000.00),
                                                                     ('11111111-1111-1111-1111-111111111112', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '00000000-0000-0000-0000-000000000001', 'WITHDRAW', 20000.00),
                                                                     ('11111111-1111-1111-1111-111111111113', 'cccccccc-cccc-cccc-cccc-cccccccccccc', '00000000-0000-0000-0000-000000000001', 'DEPOSIT', 1500.00),

                                                                     -- Транзакции для пользователя 2 (saving.user)
                                                                     ('22222222-2222-2222-2222-222222222221', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '00000000-0000-0000-0000-000000000002', 'DEPOSIT', 75000.00),
                                                                     ('22222222-2222-2222-2222-222222222222', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '00000000-0000-0000-0000-000000000002', 'WITHDRAW', 25000.00),

                                                                     -- Транзакции для пользователя 3 (credit.user)
                                                                     ('33333333-3333-3333-3333-333333333331', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '00000000-0000-0000-0000-000000000003', 'DEPOSIT', 300000.00),
                                                                     ('33333333-3333-3333-3333-333333333332', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '00000000-0000-0000-0000-000000000003', 'WITHDRAW', 150000.00),

                                                                     -- Транзакции для пользователя 5 (active.user)
                                                                     ('55555555-5555-5555-5555-555555555551', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '00000000-0000-0000-0000-000000000005', 'DEPOSIT', 10000.00),
                                                                     ('55555555-5555-5555-5555-555555555552', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '00000000-0000-0000-0000-000000000005', 'WITHDRAW', 5000.00),
                                                                     ('55555555-5555-5555-5555-555555555553', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '00000000-0000-0000-0000-000000000005', 'DEPOSIT', 15000.00),
                                                                     ('55555555-5555-5555-5555-555555555554', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '00000000-0000-0000-0000-000000000005', 'WITHDRAW', 7000.00),
                                                                     ('55555555-5555-5555-5555-555555555555', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '00000000-0000-0000-0000-000000000005', 'DEPOSIT', 20000.00),

                                                                     -- Транзакция для пользователя 4 (basic.user)
                                                                     ('44444444-4444-4444-4444-444444444441', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '00000000-0000-0000-0000-000000000004', 'DEPOSIT', 1000.00);