package org.skypro.bank.star.recommendations_service.integration_tests;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Быстрый диагностический тест для выявления проблем с H2 in-memory базой
 */
@SpringBootTest
@ActiveProfiles("test")
//@Import(org.skypro.bank.star.recommendations_service.configuration.TestDatabaseConfig.class)
public class QuickDiagnosticTest {

    private static final Logger logger = LoggerFactory.getLogger(QuickDiagnosticTest.class);

    @Autowired
    @Qualifier("recommendationsJdbcTemplate")
    private JdbcTemplate recommendationsJdbcTemplate;

    @Test
    void quickDiagnostic() {
        logger.info("=== 🚨 БЫСТРАЯ ДИАГНОСТИКА H2 IN-MEMORY БАЗЫ ===");

        // 1. Проверим базовое подключение к БД
        logger.info("1. ПРОВЕРКА ПОДКЛЮЧЕНИЯ К БД:");
        try {
            String dbUrl = recommendationsJdbcTemplate.getDataSource().getConnection().getMetaData().getURL();
            String dbUser = recommendationsJdbcTemplate.getDataSource().getConnection().getMetaData().getUserName();
            logger.info("   ✅ URL БД: {}", dbUrl);
            logger.info("   ✅ Пользователь БД: {}", dbUser);
        } catch (Exception e) {
            logger.error("   ❌ Ошибка подключения к БД: {}", e.getMessage());
            fail("Не удалось подключиться к БД: " + e.getMessage());
        }

        // 2. Проверим выполнение простого SQL запроса
        logger.info("2. ПРОВЕРКА ВЫПОЛНЕНИЯ SQL ЗАПРОСОВ:");
        try {
            Integer result = recommendationsJdbcTemplate.queryForObject("SELECT 1", Integer.class);
            logger.info("   ✅ Простой SQL запрос выполнен: SELECT 1 = {}", result);
            assertEquals(1, result, "Запрос SELECT 1 должен возвращать 1");
        } catch (Exception e) {
            logger.error("   ❌ Ошибка выполнения SQL запроса: {}", e.getMessage());
            fail("Не удалось выполнить SQL запрос: " + e.getMessage());
        }

        // 3. Проверим все схемы в БД
        logger.info("3. ПРОВЕРКА СХЕМ В БАЗЕ ДАННЫХ:");
        try {
            List<String> schemas = recommendationsJdbcTemplate.queryForList(
                    "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA", String.class);
            logger.info("   ✅ Найдено схем: {}", schemas.size());
            schemas.forEach(schema -> logger.info("      - {}", schema));

            assertTrue(schemas.contains("PUBLIC"), "Должна быть схема PUBLIC");
        } catch (Exception e) {
            logger.error("   ❌ Ошибка получения списка схем: {}", e.getMessage());
        }

        // 4. Проверим все таблицы во всех схемах
        logger.info("4. ПРОВЕРКА ТАБЛИЦ ВО ВСЕХ СХЕМАХ:");
        try {
            List<Map<String, Object>> allTables = recommendationsJdbcTemplate.queryForList(
                    "SELECT TABLE_SCHEMA, TABLE_NAME FROM INFORMATION_SCHEMA.TABLES ORDER BY TABLE_SCHEMA, TABLE_NAME");

            logger.info("   ✅ Найдено таблиц: {}", allTables.size());
            if (allTables.isEmpty()) {
                logger.info("   ⚠️  В базе данных нет ни одной таблицы!");
            } else {
                allTables.forEach(table -> {
                    String schema = (String) table.get("TABLE_SCHEMA");
                    String tableName = (String) table.get("TABLE_NAME");
                    logger.info("      - {}.{}", schema, tableName);
                });
            }
        } catch (Exception e) {
            logger.error("   ❌ Ошибка получения списка таблиц: {}", e.getMessage());
        }

        // 5. Проверим, применяются ли Liquibase миграции
        logger.info("5. ПРОВЕРКА LIQUIBASE МИГРАЦИЙ:");
        try {
            List<Map<String, Object>> changelog = recommendationsJdbcTemplate.queryForList(
                    "SELECT * FROM DATABASECHANGELOG");
            logger.info("   ✅ Liquibase changelog: {} применённых миграций", changelog.size());

            if (!changelog.isEmpty()) {
                changelog.forEach(change -> {
                    String id = (String) change.get("ID");
                    String author = (String) change.get("AUTHOR");
                    String filename = (String) change.get("FILENAME");
                    logger.info("      - {} ({}): {}", id, author, filename);
                });
            } else {
                logger.info("   ⚠️  Таблица DATABASECHANGELOG пуста - миграции не применялись");
            }
        } catch (Exception e) {
            logger.error("   ❌ Таблица DATABASECHANGELOG не найдена: {}", e.getMessage());
            logger.info("   ℹ️  Это означает, что Liquibase не применял миграции");
        }

        // 6. Проверим настройки Liquibase из Spring контекста
        logger.info("6. ПРОВЕРКА НАСТРОЕК LIQUIBASE:");
        try {
            // Попробуем создать простую тестовую таблицу напрямую
            recommendationsJdbcTemplate.execute("CREATE TABLE IF NOT EXISTS test_diagnostic (id INT PRIMARY KEY, name VARCHAR(50))");
            logger.info("   ✅ Успешно создана тестовая таблица");

            // Проверим, что она появилась
            List<String> tablesAfter = recommendationsJdbcTemplate.queryForList(
                    "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'TEST_DIAGNOSTIC'",
                    String.class);
            logger.info("   ✅ Тестовая таблица в БД: {}", !tablesAfter.isEmpty());

            // Удалим тестовую таблицу
            recommendationsJdbcTemplate.execute("DROP TABLE IF EXISTS test_diagnostic");

        } catch (Exception e) {
            logger.error("   ❌ Ошибка создания тестовой таблицы: {}", e.getMessage());
        }

        // 7. Проверим конфигурацию DataSource
        logger.info("7. ПРОВЕРКА КОНФИГУРАЦИИ DATASOURCE:");
        try {
            String datasourceClass = recommendationsJdbcTemplate.getDataSource().getClass().getName();
            logger.info("   ✅ DataSource класс: {}", datasourceClass);

            // Проверим URL DataSource
            String jdbcUrl = recommendationsJdbcTemplate.getDataSource().getConnection().getMetaData().getURL();
            logger.info("   ✅ JDBC URL: {}", jdbcUrl);

            // Проверим, это H2 in-memory?
            if (jdbcUrl.contains("h2:mem")) {
                logger.info("   ✅ Используется H2 in-memory база данных");
            } else {
                logger.warn("   ⚠️  Используется НЕ H2 in-memory база: {}", jdbcUrl);
            }

        } catch (Exception e) {
            logger.error("   ❌ Ошибка проверки DataSource: {}", e.getMessage());
        }

        logger.info("=== 🎯 ДИАГНОСТИКА ЗАВЕРШЕНА ===");

        // Финальная проверка - можем ли мы создавать и использовать таблицы
        logger.info("ФИНАЛЬНАЯ ПРОВЕРКА:");
        try {
            // Создаём временную таблицу
            recommendationsJdbcTemplate.execute("CREATE TEMPORARY TABLE temp_diagnostic (id INT, message VARCHAR(100))");

            // Вставляем данные
            recommendationsJdbcTemplate.update("INSERT INTO temp_diagnostic (id, message) VALUES (1, 'Тест успешен!')");

            // Читаем данные
            String message = recommendationsJdbcTemplate.queryForObject(
                    "SELECT message FROM temp_diagnostic WHERE id = 1", String.class);

            logger.info("   ✅ Чтение/запись в БД работают: {}", message);
            assertEquals("Тест успешен!", message);

        } catch (Exception e) {
            logger.error("   ❌ Ошибка работы с БД: {}", e.getMessage());
            fail("Базовые операции с БД не работают: " + e.getMessage());
        }

        logger.info("=== ✅ ДИАГНОСТИКА ПРОЙДЕНА УСПЕШНО ===");
    }
}
