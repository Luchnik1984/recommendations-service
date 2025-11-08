
-- Liquibase миграция для создания таблиц динамических правил рекомендаций
--
-- Эта миграция создает структуру БД для хранения динамических правил:
-- - dynamic_rules - основная таблица правил
-- - rule_queries - таблица запросов (условий) правил
-- - rule_query_arguments - таблица аргументов запросов
-- ==========================================================================

-- Таблица для хранения динамических правил рекомендаций
CREATE TABLE dynamic_rules (
        id UUID NOT NULL PRIMARY KEY,
        product_name VARCHAR(255) NOT NULL,
        product_id UUID NOT NULL,
        product_text TEXT NOT NULL
);

COMMENT ON TABLE dynamic_rules IS 'Таблица динамических правил рекомендаций банковских продуктов';

-- ==========================================================================

CREATE INDEX idx_dynamic_rules_product_id ON dynamic_rules(product_id);
CREATE INDEX idx_dynamic_rules_product_name ON dynamic_rules(product_name);


-- Таблица для хранения запросов правил
CREATE TABLE rule_queries (
        id BIGSERIAL NOT NULL PRIMARY KEY,
        dynamic_rule_id UUID NOT NULL,
        query_type VARCHAR(50) NOT NULL,
        negate BOOLEAN NOT NULL DEFAULT false,
        CONSTRAINT fk_rule_queries_dynamic_rule
            FOREIGN KEY (dynamic_rule_id)
            REFERENCES dynamic_rules(id)
            ON DELETE CASCADE,
        CONSTRAINT chk_valid_query_type
            CHECK (query_type IN (
            'USER_OF',
            'ACTIVE_USER_OF',
            'TRANSACTION_SUM_COMPARE',
            'TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW'
            ))
);

COMMENT ON TABLE rule_queries IS 'Таблица запросов (условий) в составе динамических правил';

CREATE INDEX idx_rule_queries_dynamic_rule_id ON rule_queries(dynamic_rule_id);
CREATE INDEX idx_rule_queries_query_type ON rule_queries(query_type);
-- ==========================================================================

-- Таблица для хранения аргументов запросов
CREATE TABLE rule_query_arguments (
         id BIGSERIAL NOT NULL PRIMARY KEY,
            rule_query_id BIGINT NOT NULL,
            argument_value VARCHAR(255) NOT NULL,
            argument_order INTEGER NOT NULL DEFAULT 0,

    -- Внешний ключ на таблицу rule_queries
        CONSTRAINT fk_rule_query_arguments_rule_query
            FOREIGN KEY (rule_query_id)
            REFERENCES rule_queries(id)
            ON DELETE CASCADE,
        CONSTRAINT chk_argument_order_non_negative
            CHECK (argument_order >= 0),
        CONSTRAINT chk_argument_value_not_empty
            CHECK (LENGTH(TRIM(argument_value)) > 0),

    -- Уникальность комбинации запрос-порядок
        CONSTRAINT uk_rule_query_arguments_order
            UNIQUE (rule_query_id, argument_order)
);

COMMENT ON TABLE rule_query_arguments IS 'Таблица аргументов запросов динамических правил';

CREATE INDEX idx_rule_query_arguments_rule_query_id ON rule_query_arguments(rule_query_id);
CREATE INDEX idx_rule_query_arguments_order ON rule_query_arguments(argument_order);