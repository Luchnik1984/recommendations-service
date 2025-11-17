package org.skypro.bank.star.recommendations_service.integration_tests;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * МИНИМАЛЬНЫЙ ТЕСТ H2 БАЗЫ БЕЗ SPRING BOOT
 * Создает базу вручную и проверяет таблицы
 */
public class MinimalH2Test {

    private static final Logger logger = LoggerFactory.getLogger(MinimalH2Test.class);

    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        // Создаем H2 in-memory базу вручную
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:test_minimal;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        dataSource.setUser("sa");
        dataSource.setPassword("");

        this.jdbcTemplate = new JdbcTemplate(dataSource);

        // Очищаем и создаем таблицы
        dropTablesIfExist();
        createTables();
        insertTestData();
    }

    private void dropTablesIfExist() {
        logger.info("=== ОЧИСТКА ТАБЛИЦ ===");
        try {
            jdbcTemplate.execute("DROP TABLE IF EXISTS transactions");
            jdbcTemplate.execute("DROP TABLE IF EXISTS products");
            jdbcTemplate.execute("DROP TABLE IF EXISTS users");
            logger.info("✅ Таблицы очищены");
        } catch (Exception e) {
            logger.info("Таблицы не существовали, создаем новые");
        }
    }

    private void createTables() {
        logger.info("=== СОЗДАНИЕ ТАБЛИЦ H2 ===");

        // Таблица USERS
        jdbcTemplate.execute("""
            CREATE TABLE users (
                id UUID PRIMARY KEY,
                username VARCHAR(255) NOT NULL,
                first_name VARCHAR(255),
                last_name VARCHAR(255)
            )
        """);

        // Таблица PRODUCTS
        jdbcTemplate.execute("""
            CREATE TABLE products (
                id UUID PRIMARY KEY,
                type VARCHAR(255) NOT NULL,
                name VARCHAR(255) NOT NULL
            )
        """);

        // Таблица TRANSACTIONS
        jdbcTemplate.execute("""
            CREATE TABLE transactions (
                id UUID PRIMARY KEY,
                product_id UUID NOT NULL,
                user_id UUID NOT NULL,
                type VARCHAR(255) NOT NULL CHECK (type IN ('DEPOSIT', 'WITHDRAW')),
                amount INTEGER NOT NULL CHECK (amount >= 0),
                CONSTRAINT fk_transactions_product_id FOREIGN KEY (product_id) REFERENCES products(id)
            )
        """);

        logger.info("✅ Таблицы созданы");
    }

    private void insertTestData() {
        logger.info("=== ЗАГРУЗКА ТЕСТОВЫХ ДАННЫХ ===");

        // Пользователи
        jdbcTemplate.update("INSERT INTO users (id, username, first_name, last_name) VALUES (?, ?, ?, ?)",
                "00000000-0000-0000-0000-000000000001", "test.user", "Тест", "Пользователь");

        // Продукты
        jdbcTemplate.update("INSERT INTO products (id, type, name) VALUES (?, ?, ?)",
                "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa", "DEBIT", "Дебетовая карта");

        // Транзакции - добавляем оба типа
        jdbcTemplate.update("INSERT INTO transactions (id, product_id, user_id, type, amount) VALUES (?, ?, ?, ?, ?)",
                "11111111-1111-1111-1111-111111111111",
                "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa",
                "00000000-0000-0000-0000-000000000001",
                "DEPOSIT", 100000);

        jdbcTemplate.update("INSERT INTO transactions (id, product_id, user_id, type, amount) VALUES (?, ?, ?, ?, ?)",
                "22222222-2222-2222-2222-222222222222",
                "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa",
                "00000000-0000-0000-0000-000000000001",
                "WITHDRAW", 50000);

        logger.info("✅ Тестовые данные загружены");
    }

    @Test
    void testH2DatabaseWorks() {
        logger.info("=== ТЕСТ H2 БАЗЫ ===");

        // Проверяем подключение
        Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        assertEquals(1, result);
        logger.info("✅ H2 база подключена");

        // Проверяем таблицы
        checkTableExists("USERS");
        checkTableExists("PRODUCTS");
        checkTableExists("TRANSACTIONS");

        // Проверяем данные
        int usersCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM USERS", Integer.class);
        int productsCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM PRODUCTS", Integer.class);
        int transactionsCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM TRANSACTIONS", Integer.class);

        logger.info("Пользователей: {}, Продуктов: {}, Транзакций: {}",
                usersCount, productsCount, transactionsCount);

        assertTrue(usersCount > 0, "Должны быть тестовые пользователи");
        assertTrue(productsCount > 0, "Должны быть тестовые продукты");
        assertTrue(transactionsCount > 0, "Должны быть тестовые транзакции");

        logger.info("✅ Все таблицы H2 созданы и содержат данные");
    }

    @Test
    void testTransactionStructure() {
        logger.info("=== ПРОВЕРКА СТРУКТУРЫ ТРАНЗАКЦИЙ ===");

        // В H2 используем DATA_TYPE вместо TYPE_NAME
        List<Map<String, Object>> columns = jdbcTemplate.queryForList(
                "SELECT COLUMN_NAME, DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS " +
                        "WHERE TABLE_NAME = 'TRANSACTIONS' AND COLUMN_NAME = 'AMOUNT'"
        );

        assertFalse(columns.isEmpty(), "Колонка AMOUNT должна существовать");

        // Проверяем что тип INTEGER
        Map<String, Object> amountColumn = columns.get(0);
        String dataType = (String) amountColumn.get("DATA_TYPE");
        assertEquals("INTEGER", dataType.toUpperCase(), "Тип AMOUNT должен быть INTEGER");

        logger.info("✅ Колонка AMOUNT имеет тип: {}", dataType);

        // Проверяем допустимые типы транзакций
        List<String> transactionTypes = jdbcTemplate.queryForList(
                "SELECT DISTINCT TYPE FROM TRANSACTIONS",
                String.class
        );

        assertTrue(transactionTypes.contains("DEPOSIT"), "Должен быть тип DEPOSIT");
        assertTrue(transactionTypes.contains("WITHDRAW"), "Должен быть тип WITHDRAW");
        logger.info("✅ Допустимые типы транзакций: {}", transactionTypes);

        logger.info("✅ Структура TRANSACTIONS соответствует продакшен");
    }

    @Test
    void testForeignKeyConstraints() {
        logger.info("=== ПРОВЕРКА ВНЕШНИХ КЛЮЧЕЙ ===");

        // В H2 используем правильный запрос для внешних ключей
        List<Map<String, Object>> constraints = jdbcTemplate.queryForList(
                "SELECT * FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS " +
                        "WHERE TABLE_NAME = 'TRANSACTIONS' AND CONSTRAINT_TYPE = 'FOREIGN KEY'"
        );

        assertFalse(constraints.isEmpty(), "Должен быть внешний ключ в TRANSACTIONS");
        logger.info("✅ Внешний ключ существует: {}", constraints.get(0).get("CONSTRAINT_NAME"));

        logger.info("✅ Внешние ключи настроены правильно");
    }

    @Test
    void testDataIntegrity() {
        logger.info("=== ПРОВЕРКА ЦЕЛОСТНОСТИ ДАННЫХ ===");

        // Проверяем что сумма транзакций правильная
        Integer totalDeposits = jdbcTemplate.queryForObject(
                "SELECT SUM(amount) FROM TRANSACTIONS WHERE type = 'DEPOSIT'",
                Integer.class
        );

        Integer totalWithdrawals = jdbcTemplate.queryForObject(
                "SELECT SUM(amount) FROM TRANSACTIONS WHERE type = 'WITHDRAW'",
                Integer.class
        );

        assertEquals(100000, totalDeposits, "Сумма DEPOSIT должна быть 100000");
        assertEquals(50000, totalWithdrawals, "Сумма WITHDRAW должна быть 50000");

        logger.info("✅ Суммы транзакций правильные: DEPOSIT={}, WITHDRAW={}", totalDeposits, totalWithdrawals);

        // Проверяем что внешний ключ работает
        try {
            // Попытка вставить транзакцию с несуществующим product_id должна вызвать ошибку
            jdbcTemplate.update("INSERT INTO transactions (id, product_id, user_id, type, amount) VALUES (?, ?, ?, ?, ?)",
                    "33333333-3333-3333-3333-333333333333",
                    "ffffffff-ffff-ffff-ffff-ffffffffffff", // несуществующий product_id
                    "00000000-0000-0000-0000-000000000001",
                    "DEPOSIT", 1000);
            fail("Должна быть ошибка внешнего ключа");
        } catch (Exception e) {
            logger.info("✅ Внешний ключ работает: {}", e.getMessage().contains("foreign key"));
        }

        logger.info("✅ Целостность данных обеспечена");
    }

    private void checkTableExists(String tableName) {
        try {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = ?",
                    Integer.class, tableName);
            assertTrue(count > 0, "Таблица " + tableName + " должна существовать");
            logger.info("✅ Таблица {} существует", tableName);
        } catch (Exception e) {
            fail("Таблица " + tableName + " не найдена: " + e.getMessage());
        }
    }
}