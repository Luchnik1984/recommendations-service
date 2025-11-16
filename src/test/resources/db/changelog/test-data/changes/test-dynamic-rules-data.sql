-- ТЕСТОВЫЕ ДАННЫЕ ДЛЯ ДИНАМИЧЕСКИХ ПРАВИЛ
-- вставляем только если таблицы пустые (для повторного использования БД)

-- Очистка существующих тестовых данных (но не структуры!)
DELETE FROM rule_query_arguments;
DELETE FROM rule_queries;
DELETE FROM dynamic_rules;

-- Динамическое правило 1: Премиальная кредитная карта
INSERT INTO dynamic_rules (id, product_name, product_id, product_text) VALUES
    ('d1111111-1111-1111-1111-111111111111', 'Премиальная кредитная карта',
     'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee',
     'Получите премиальную кредитную карту с повышенным кэшбэком и привилегиями');

-- Запросы для правила 1
INSERT INTO rule_queries (id, dynamic_rule_id, query_type, negate, query_order) VALUES
                                                                                    (1, 'd1111111-1111-1111-1111-111111111111', 'USER_OF', false, 0),
                                                                                    (2, 'd1111111-1111-1111-1111-111111111111', 'ACTIVE_USER_OF', false, 1),
                                                                                    (3, 'd1111111-1111-1111-1111-111111111111', 'TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW', false, 2);

-- Аргументы для запросов правила 1
INSERT INTO rule_query_arguments (rule_query_id, argument_value, argument_order) VALUES
                                                                                     (1, 'DEBIT', 0),
                                                                                     (2, 'DEBIT', 0),
                                                                                     (3, 'DEBIT', 0),
                                                                                     (3, '>', 1);

-- Динамическое правило 2: Инвестиционный портфель
INSERT INTO dynamic_rules (id, product_name, product_id, product_text) VALUES
    ('d2222222-2222-2222-2222-222222222222', 'Инвестиционный портфель',
     'ffffffff-ffff-ffff-ffff-ffffffffffff',
     'Создайте диверсифицированный инвестиционный портфель с профессиональным управлением');

-- Запросы для правила 2
INSERT INTO rule_queries (id, dynamic_rule_id, query_type, negate, query_order) VALUES
                                                                                    (4, 'd2222222-2222-2222-2222-222222222222', 'USER_OF', false, 0),
                                                                                    (5, 'd2222222-2222-2222-2222-222222222222', 'USER_OF', true, 1),
                                                                                    (6, 'd2222222-2222-2222-2222-222222222222', 'TRANSACTION_SUM_COMPARE', false, 2);

-- Аргументы для запросов правила 2
INSERT INTO rule_query_arguments (rule_query_id, argument_value, argument_order) VALUES
                                                                                     (4, 'DEBIT', 0),
                                                                                     (5, 'INVEST', 0),
                                                                                     (6, 'SAVING', 0),
                                                                                     (6, 'DEPOSIT', 1),
                                                                                     (6, '>', 2),
                                                                                     (6, '5000', 3);