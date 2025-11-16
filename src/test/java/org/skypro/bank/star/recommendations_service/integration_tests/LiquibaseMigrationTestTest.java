package org.skypro.bank.star.recommendations_service.integration_tests;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ТЕСТ ДЛЯ ПРИМЕНЕНИЯ MIGRATIONS В НОВОЙ БД.
 * Запустите этот тест ПЕРВЫМ при настройке новой тестовой БД
 */
@SpringBootTest
@ActiveProfiles("test")
public class LiquibaseMigrationTestTest {
    private static final Logger logger = LoggerFactory.getLogger(LiquibaseMigrationTest.class);

    @Autowired
    private DataSource dataSource; // Будет использован dynamicRulesDataSource

    @Test
    void testLiquibaseMigrationsApplied() {
        logger.info("=== ПРОВЕРКА И ПРИМЕНЕНИЕ LIQUIBASE MIGRATIONS ===");

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        try {
            // 1. Проверяем, что таблица DATABASECHANGELOG существует (значит миграции применялись)
            List<Map<String, Object>> changelog = jdbcTemplate.queryForList(
                    "SELECT * FROM DATABASECHANGELOG"
            );
            logger.info("✅ Таблица DATABASECHANGELOG найдена, применено миграций: {}", changelog.size());

            // 2. Проверяем, что наши таблицы созданы
            checkTableExists(jdbcTemplate, "dynamic_rules");
            checkTableExists(jdbcTemplate, "rule_queries");
            checkTableExists(jdbcTemplate, "rule_query_arguments");

            logger.info("=== ✅ ВСЕ MIGRATIONS ПРИМЕНЕНЫ УСПЕШНО ===");

        } catch (Exception e) {
            logger.error("❌ Ошибка при проверке миграций: {}", e.getMessage());
            fail("Liquibase миграции не применены: " + e.getMessage());
        }
    }

    private void checkTableExists(JdbcTemplate jdbcTemplate, String tableName) {
        try {
            // Проверяем существование таблицы
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = ?",
                    Integer.class, tableName.toLowerCase()
            );

            if (count != null && count > 0) {
                logger.info("✅ Таблица {} существует", tableName);

                // Проверяем что таблица не пустая (опционально)
                Integer rowCount = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM " + tableName, Integer.class
                );
                logger.info("   Записей в таблице {}: {}", tableName, rowCount);
            } else {
                fail("Таблица " + tableName + " не найдена в БД");
            }
        } catch (Exception e) {
            fail("Ошибка проверки таблицы " + tableName + ": " + e.getMessage());
        }
    }
}
