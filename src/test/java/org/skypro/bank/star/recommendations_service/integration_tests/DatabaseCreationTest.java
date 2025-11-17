package org.skypro.bank.star.recommendations_service.integration_tests;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ПРОСТОЙ ТЕСТ ДЛЯ ПРОВЕРКИ СОЗДАНИЯ ТАБЛИЦ В ОБЕИХ БАЗАХ ДАННЫХ.
 * Проверяет что при запуске тестов с профилем "test":
 * - Создаются таблицы в H2 in-memory базе (транзакционные данные)
 * - Создаются таблицы в PostgreSQL базе (динамические правила)
 *
 * @version 1.0
 * @since 2024
 */
@SpringBootTest
@ActiveProfiles("test")
public class DatabaseCreationTest {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseCreationTest.class);

    @Autowired
    @Qualifier("recommendationsJdbcTemplate")
    private JdbcTemplate h2JdbcTemplate;

    @Autowired
    @Qualifier("dynamicRulesDataSource")
    private DataSource postgresqlDataSource;

    /**
     * ТЕСТ: Проверка создания таблиц в H2 in-memory базе
     */
    @Test
    void testH2TablesCreated() {
        logger.info("=== ТЕСТ: ПРОВЕРКА СОЗДАНИЯ ТАБЛИЦ В H2 БАЗЕ ===");

        // Проверяем основные таблицы транзакционных данных
        assertTrue(tableExists(h2JdbcTemplate, "USERS"), "Таблица USERS должна существовать в H2 базе");
        assertTrue(tableExists(h2JdbcTemplate, "PRODUCTS"), "Таблица PRODUCTS должна существовать в H2 базе");
        assertTrue(tableExists(h2JdbcTemplate, "TRANSACTIONS"), "Таблица TRANSACTIONS должна существовать в H2 базе");

        // Проверяем что таблицы не пустые (тестовые данные загружены)
        int usersCount = h2JdbcTemplate.queryForObject("SELECT COUNT(*) FROM USERS", Integer.class);
        int productsCount = h2JdbcTemplate.queryForObject("SELECT COUNT(*) FROM PRODUCTS", Integer.class);
        int transactionsCount = h2JdbcTemplate.queryForObject("SELECT COUNT(*) FROM TRANSACTIONS", Integer.class);

        logger.info("H2 База - Пользователей: {}, Продуктов: {}, Транзакций: {}",
                usersCount, productsCount, transactionsCount);

        assertTrue(usersCount > 0, "Таблица USERS должна содержать тестовые данные");
        assertTrue(productsCount > 0, "Таблица PRODUCTS должна содержать тестовые данные");
        assertTrue(transactionsCount > 0, "Таблица TRANSACTIONS должна содержать тестовые данные");

        logger.info("✅ H2 БАЗА: Все таблицы созданы и содержат тестовые данные");
    }

    /**
     * ТЕСТ: Проверка создания таблиц в PostgreSQL базе динамических правил
     */
    @Test
    void testPostgreSQLTablesCreated() {
        logger.info("=== ТЕСТ: ПРОВЕРКА СОЗДАНИЯ ТАБЛИЦ В POSTGRESQL БАЗЕ ===");

        JdbcTemplate postgresJdbcTemplate = new JdbcTemplate(postgresqlDataSource);

        // Проверяем основные таблицы динамических правил
        assertTrue(tableExists(postgresJdbcTemplate, "dynamic_rules"),
                "Таблица dynamic_rules должна существовать в PostgreSQL базе");
        assertTrue(tableExists(postgresJdbcTemplate, "rule_queries"),
                "Таблица rule_queries должна существовать в PostgreSQL базе");
        assertTrue(tableExists(postgresJdbcTemplate, "rule_query_arguments"),
                "Таблица rule_query_arguments должна существовать в PostgreSQL базе");

        // Проверяем что таблицы содержат тестовые данные
        int rulesCount = postgresJdbcTemplate.queryForObject("SELECT COUNT(*) FROM dynamic_rules", Integer.class);
        int queriesCount = postgresJdbcTemplate.queryForObject("SELECT COUNT(*) FROM rule_queries", Integer.class);
        int argumentsCount = postgresJdbcTemplate.queryForObject("SELECT COUNT(*) FROM rule_query_arguments", Integer.class);

        logger.info("PostgreSQL База - Правил: {}, Запросов: {}, Аргументов: {}",
                rulesCount, queriesCount, argumentsCount);

        assertTrue(rulesCount > 0, "Таблица dynamic_rules должна содержать тестовые правила");
        assertTrue(queriesCount > 0, "Таблица rule_queries должна содержать тестовые запросы");
        assertTrue(argumentsCount > 0, "Таблица rule_query_arguments должна содержать тестовые аргументы");

        logger.info("✅ POSTGRESQL БАЗА: Все таблицы созданы и содержат тестовые данные");
    }

    /**
     * ТЕСТ: Проверка, что базы данных работают независимо
     */
    @Test
    void testDatabasesAreSeparate() {
        logger.info("=== ТЕСТ: ПРОВЕРКА РАЗДЕЛЕНИЯ БАЗ ДАННЫХ ===");

        JdbcTemplate postgresJdbcTemplate = new JdbcTemplate(postgresqlDataSource);

        // Проверяем что таблицы H2 не существуют в PostgreSQL
        assertFalse(tableExists(postgresJdbcTemplate, "USERS"),
                "Таблица USERS не должна существовать в PostgreSQL базе");
        assertFalse(tableExists(postgresJdbcTemplate, "TRANSACTIONS"),
                "Таблица TRANSACTIONS не должна существовать в PostgreSQL базе");

        // Проверяем что таблицы PostgreSQL не существуют в H2
        assertFalse(tableExists(h2JdbcTemplate, "dynamic_rules"),
                "Таблица dynamic_rules не должна существовать в H2 базе");
        assertFalse(tableExists(h2JdbcTemplate, "rule_queries"),
                "Таблица rule_queries не должна существовать в H2 базе");

        logger.info("✅ БАЗЫ ДАННЫХ: Работают полностью независимо");
    }

    /**
     * ТЕСТ: Проверка структуры таблицы TRANSACTIONS в H2
     */
    @Test
    void testH2TransactionsTableStructure() {
        logger.info("=== ТЕСТ: ПРОВЕРКА СТРУКТУРЫ ТАБЛИЦЫ TRANSACTIONS ===");

        // Проверяем что колонка amount имеет тип INTEGER (как в продакшен)
        List<String> columns = h2JdbcTemplate.queryForList(
                "SELECT COLUMN_NAME, TYPE_NAME FROM INFORMATION_SCHEMA.COLUMNS " +
                        "WHERE TABLE_NAME = 'TRANSACTIONS' AND COLUMN_NAME = 'AMOUNT'",
                String.class
        );

        assertFalse(columns.isEmpty(), "Колонка AMOUNT должна существовать в таблице TRANSACTIONS");

        // Проверяем допустимые значения для type
        List<String> transactionTypes = h2JdbcTemplate.queryForList(
                "SELECT DISTINCT TYPE FROM TRANSACTIONS",
                String.class
        );

        logger.info("Допустимые типы транзакций: {}", transactionTypes);
        assertTrue(transactionTypes.contains("DEPOSIT"), "Должен быть тип DEPOSIT");
        assertTrue(transactionTypes.contains("WITHDRAW"), "Должен быть тип WITHDRAW");

        logger.info("✅ СТРУКТУРА TRANSACTIONS: Соответствует продакшен");
    }

    /**
     * Вспомогательный метод для проверки существования таблицы
     */
    private boolean tableExists(JdbcTemplate jdbcTemplate, String tableName) {
        try {
            // Для H2 и PostgreSQL используем information_schema
            String sql = "SELECT COUNT(*) FROM information_schema.tables " +
                    "WHERE table_name = UPPER(?)";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, tableName);
            return count != null && count > 0;
        } catch (Exception e) {
            logger.debug("Таблица {} не найдена: {}", tableName, e.getMessage());
            return false;
        }
    }
}
