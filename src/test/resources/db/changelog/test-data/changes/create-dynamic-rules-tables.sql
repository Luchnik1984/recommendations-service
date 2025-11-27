-- ==========================================================================
-- LIQUIBASE MIGRATION: СОЗДАНИЕ СТРУКТУРЫ ДЛЯ ДИНАМИЧЕСКИХ ПРАВИЛ
-- ТОЧНАЯ КОПИЯ ПРОДАКШЕН СТРУКТУРЫ БАЗЫ DYNAMIC_RULES
-- ==========================================================================

-- Changeset: create_dynamic_rules_table
-- Comment: Создание таблицы динамических правил (точная копия продакшн)

CREATE TABLE IF NOT EXISTS dynamic_rules (
    id UUID NOT NULL PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    product_id UUID NOT NULL,
    product_text TEXT NOT NULL
    );

COMMENT ON TABLE dynamic_rules IS 'Таблица динамических правил рекомендаций банковских продуктов';

-- Changeset: create_rule_queries_table
-- Comment: Создание таблицы запросов правил

CREATE TABLE IF NOT EXISTS rule_queries (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    dynamic_rule_id UUID NOT NULL,
    query_type VARCHAR(50) NOT NULL,
    negate BOOLEAN NOT NULL DEFAULT false,
    query_order INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT fk_rule_queries_dynamic_rule
    FOREIGN KEY (dynamic_rule_id)
    REFERENCES dynamic_rules(id)
    ON DELETE CASCADE
    );

COMMENT ON TABLE rule_queries IS 'Таблица запросов (условий) в составе динамических правил';

-- Changeset: create_rule_query_arguments_table
-- Comment: Создание таблицы аргументов запросов

CREATE TABLE IF NOT EXISTS rule_query_arguments (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    rule_query_id BIGINT NOT NULL,
    argument_value VARCHAR(255) NOT NULL,
    argument_order INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT fk_rule_query_arguments_rule_query
    FOREIGN KEY (rule_query_id)
    REFERENCES rule_queries(id)
    ON DELETE CASCADE
    );

COMMENT ON TABLE rule_query_arguments IS 'Таблица аргументов запросов динамических правил';

-- ==========================================================================
-- СОЗДАНИЕ ИНДЕКСОВ (ТОЧНАЯ КОПИЯ ПРОДАКШЕН ИНДЕКСОВ)
-- ==========================================================================

-- Changeset: create_dynamic_rules_indexes
-- Comment: Создание индексов для таблицы dynamic_rules

CREATE INDEX IF NOT EXISTS idx_dynamic_rules_product_id ON dynamic_rules(product_id);
CREATE INDEX IF NOT EXISTS idx_dynamic_rules_product_name ON dynamic_rules(product_name);

-- Changeset: create_rule_queries_indexes
-- Comment: Создание индексов для таблицы rule_queries

CREATE INDEX IF NOT EXISTS idx_rule_queries_dynamic_rule_id ON rule_queries(dynamic_rule_id);
CREATE INDEX IF NOT EXISTS idx_rule_queries_query_type ON rule_queries(query_type);
CREATE INDEX IF NOT EXISTS idx_rule_queries_query_order ON rule_queries(query_order);

-- Changeset: create_rule_query_arguments_indexes
-- Comment: Создание индексов для таблицы rule_query_arguments

CREATE INDEX IF NOT EXISTS idx_rule_query_arguments_rule_query_id ON rule_query_arguments(rule_query_id);
CREATE INDEX IF NOT EXISTS idx_rule_query_arguments_order ON rule_query_arguments(argument_order);