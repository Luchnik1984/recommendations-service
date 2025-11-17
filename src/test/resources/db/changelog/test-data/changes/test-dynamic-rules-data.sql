-- ==========================================================================
-- LIQUIBASE MIGRATION: ТЕСТОВЫЕ ДАННЫЕ ДЛЯ ДИНАМИЧЕСКИХ ПРАВИЛ
-- СОЗДАНИЕ РЕАЛИСТИЧНЫХ ТЕСТОВЫХ ПРАВИЛ ДЛЯ ПРОВЕРКИ РЕКОМЕНДАЦИЙ
-- ==========================================================================

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
     'Получите премиальную кредитную карту с повышенным кэшбэком 5% на все покупки');

-- Changeset: insert_queries_for_rule_1
-- Comment: Условия для премиальной кредитной карты

INSERT INTO rule_queries (id, dynamic_rule_id, query_type, negate, query_order) VALUES
                                                                                    ('d1111111-1111-1111-1111-111111111112', 'd1111111-1111-1111-1111-111111111111', 'USER_OF', false, 0),
                                                                                    ('d1111111-1111-1111-1111-111111111113', 'd1111111-1111-1111-1111-111111111111', 'ACTIVE_USER_OF', false, 1),
                                                                                    ('d1111111-1111-1111-1111-111111111114', 'd1111111-1111-1111-1111-111111111111', 'TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW', false, 2);

-- Changeset: insert_arguments_for_rule_1
-- Comment: Аргументы для условий правила 1

INSERT INTO rule_query_arguments (rule_query_id, argument_value, argument_order) VALUES
                                                                                     ('d1111111-1111-1111-1111-111111111112', 'DEBIT', 0),
                                                                                     ('d1111111-1111-1111-1111-111111111113', 'DEBIT', 0),
                                                                                     ('d1111111-1111-1111-1111-111111111114', 'DEBIT', 0),
                                                                                     ('d1111111-1111-1111-1111-111111111115', '>', 1);

-- Changeset: insert_dynamic_rule_2
-- Comment: Правило для инвестиционного портфеля (вкладчики без инвестиций)

INSERT INTO dynamic_rules (id, product_name, product_id, product_text) VALUES
    ('d2222222-2222-2222-2222-222222222222', 'Инвестиционный портфель',
     'ffffffff-ffff-ffff-ffff-ffffffffffff',
     'Создайте диверсифицированный инвестиционный портфель с профессиональным управлением');

-- Changeset: insert_queries_for_rule_2
-- Comment: Условия для инвестиционного портфеля

INSERT INTO rule_queries (id, dynamic_rule_id, query_type, negate, query_order) VALUES
                                                                                    ('d2222222-2222-2222-2222-222222222223', 'd2222222-2222-2222-2222-222222222222', 'USER_OF', false, 0),
                                                                                    ('d2222222-2222-2222-2222-222222222224', 'd2222222-2222-2222-2222-222222222222', 'USER_OF', true, 1),
                                                                                    ('d2222222-2222-2222-2222-222222222225', 'd2222222-2222-2222-2222-222222222222', 'TRANSACTION_SUM_COMPARE', false, 2);

-- Changeset: insert_arguments_for_rule_2
-- Comment: Аргументы для условий правила 2

INSERT INTO rule_query_arguments (rule_query_id, argument_value, argument_order) VALUES
                                                                                     ('d2222222-2222-2222-2222-222222222223', 'DEBIT', 0),
                                                                                     ('d2222222-2222-2222-2222-222222222224', 'INVEST', 0),
                                                                                     ('d2222222-2222-2222-2222-222222222225', 'SAVING', 0),
                                                                                     ('d2222222-2222-2222-2222-222222222226', 'DEPOSIT', 1),
                                                                                     ('d2222222-2222-2222-2222-222222222227', '>', 2),
                                                                                     ('d2222222-2222-2222-2222-222222222228', '500000', 3);

-- Changeset: insert_dynamic_rule_3
-- Comment: Правило для сберегательного вклада (крупные вкладчики)

INSERT INTO dynamic_rules (id, product_name, product_id, product_text) VALUES
    ('d3333333-3333-3333-3333-333333333333', 'Сберегательный вклад Премиум',
     '33333333-3333-3333-3333-333333333333',
     'Откройте сберегательный вклад с повышенной процентной ставкой 7.5% годовых');

-- Changeset: insert_queries_for_rule_3
-- Comment: Условия для сберегательного вклада

INSERT INTO rule_queries (id, dynamic_rule_id, query_type, negate, query_order) VALUES
                                                                                    ('d3333333-3333-3333-3333-333333333334', 'd3333333-3333-3333-3333-333333333333', 'USER_OF', false, 0),
                                                                                    ('d3333333-3333-3333-3333-333333333335', 'd3333333-3333-3333-3333-333333333333', 'TRANSACTION_SUM_COMPARE', false, 1);

-- Changeset: insert_arguments_for_rule_3
-- Comment: Аргументы для условий правила 3

INSERT INTO rule_query_arguments (rule_query_id, argument_value, argument_order) VALUES
                                                                                     ('d3333333-3333-3333-3333-333333333334', 'SAVING', 0),
                                                                                     ('d3333333-3333-3333-3333-333333333335', 'SAVING', 0),
                                                                                     ('d3333333-3333-3333-3333-333333333336', 'DEPOSIT', 1),
                                                                                     ('d3333333-3333-3333-3333-333333333337', '>', 2),
                                                                                     ('d3333333-3333-3333-3333-333333333338', '1000000', 3);

-- Changeset: insert_dynamic_rule_4
-- Comment: Правило для кредита наличными (крупные траты)

INSERT INTO dynamic_rules (id, product_name, product_id, product_text) VALUES
    ('d4444444-4444-4444-4444-444444444444', 'Кредит наличными Экспресс',
     '44444444-4444-4444-4444-444444444444',
     'Получите кредит наличными до 1 000 000 руб. по ставке от 8.9% годовых');

-- Changeset: insert_queries_for_rule_4
-- Comment: Условия для кредита наличными

INSERT INTO rule_queries (id, dynamic_rule_id, query_type, negate, query_order) VALUES
                                                                                    ('d4444444-4444-4444-4444-444444444445', 'd4444444-4444-4444-4444-444444444444', 'USER_OF', false, 0),
                                                                                    ('d4444444-4444-4444-4444-444444444446', 'd4444444-4444-4444-4444-444444444444', 'TRANSACTION_SUM_COMPARE', false, 1);

-- Changeset: insert_arguments_for_rule_4
-- Comment: Аргументы для условий правила 4

INSERT INTO rule_query_arguments (rule_query_id, argument_value, argument_order) VALUES
                                                                                     ('d4444444-4444-4444-4444-444444444445', 'DEBIT', 0),
                                                                                     ('d4444444-4444-4444-4444-444444444446', 'DEBIT', 0),
                                                                                     ('d4444444-4444-4444-4444-444444444447', 'WITHDRAW', 1),
                                                                                     ('d4444444-4444-4444-4444-444444444448', '>', 2),
                                                                                     ('d4444444-4444-4444-4444-444444444449', '5000000', 3);