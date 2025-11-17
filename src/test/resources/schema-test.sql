CREATE TABLE IF NOT EXISTS dynamic_rules (
    id UUID PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    product_id UUID NOT NULL,
    product_text TEXT NOT NULL
    );

CREATE TABLE IF NOT EXISTS rule_queries (
    id UUID PRIMARY KEY,
    dynamic_rule_id UUID NOT NULL,
    query_type VARCHAR(50) NOT NULL,
    negate BOOLEAN NOT NULL DEFAULT false,
    query_order INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT fk_rule_queries_dynamic_rule
    FOREIGN KEY (dynamic_rule_id)
    REFERENCES dynamic_rules(id)
    ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS rule_query_arguments (
    id UUID PRIMARY KEY,
    rule_query_id UUID NOT NULL,
    argument_value VARCHAR(255) NOT NULL,
    argument_order INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT fk_rule_query_arguments_rule_query
    FOREIGN KEY (rule_query_id)
    REFERENCES rule_queries(id)
    ON DELETE CASCADE
    );

-- Indexes
CREATE INDEX IF NOT EXISTS idx_dynamic_rules_product_id ON dynamic_rules(product_id);
CREATE INDEX IF NOT EXISTS idx_rule_queries_dynamic_rule_id ON rule_queries(dynamic_rule_id);
CREATE INDEX IF NOT EXISTS idx_rule_query_arguments_rule_query_id ON rule_query_arguments(rule_query_id);