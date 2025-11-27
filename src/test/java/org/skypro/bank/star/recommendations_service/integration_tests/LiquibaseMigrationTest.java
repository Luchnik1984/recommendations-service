package org.skypro.bank.star.recommendations_service.integration_tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.skypro.bank.star.recommendations_service.repository.dynamic.DynamicRuleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("integration-test")
public class LiquibaseMigrationTest {

    private static final Logger logger = LoggerFactory.getLogger(LiquibaseMigrationTest.class);

    @BeforeAll
    static void setup() {

        loadEnvFile("configuration.env");
        loadEnvFile("configuration.env.dev");
    }

    private static void loadEnvFile(String envFileName) {
        try {
            Path envPath = Paths.get(envFileName);
            if (!Files.exists(envPath)) {
                logger.warn("File {} not found", envFileName);
                return;
            }

            Properties properties = new Properties();
            properties.load(Files.newBufferedReader(envPath));

            properties.forEach((key, value) -> {
                String keyStr = (String) key;
                String valueStr = (String) value;


                if (System.getProperty(keyStr) == null) {
                    System.setProperty(keyStr, valueStr);
                    // Маскируем пароли в логах
                    String logValue = keyStr.toLowerCase().contains("password") ? "***" : valueStr;
                    logger.debug("Setting a variable from .env: {}={}", keyStr, logValue);
                }
            });

            logger.info("Loaded {} variables from {}", properties.size(), envFileName);

        } catch (IOException e) {
            logger.error("Download error {}: {}", envFileName, e.getMessage());
        }
    }

    @Autowired
    @Qualifier("recommendationsJdbcTemplate")
    private JdbcTemplate recommendationsJdbcTemplate;

    @Autowired
    private DynamicRuleRepository dynamicRuleRepository;

    @Test
    void testPostgreSQLConnection() {
        logger.info("=== CONNECTION TEST TO POSTGRESQL  ===");

        String pgUrl = System.getProperty("POSTGRES_URL");
        String pgUser = System.getProperty("POSTGRES_USERNAME");

        logger.info("POSTGRES_URL: {}", pgUrl != null ? "entered" : "NOT entered");
        logger.info("POSTGRES_USERNAME: {}", pgUser != null ? "entered" : "NOT entered");

        assertNotNull(pgUrl, "POSTGRES_URL must be installed in the .env file");
        assertNotNull(pgUser, "POSTGRES_USERNAME must be installed in the .env file");
        assertTrue(pgUrl.contains("postgresql"), "POSTGRES_URL must contain 'postgresql'");

        try {
            long rulesCount = dynamicRuleRepository.count();
            logger.info(" PostgreSQL database is connected successfully");
            logger.info("   URL: {}", pgUrl);
            logger.info("   Dynamic rules: {}", rulesCount);

            assertTrue(rulesCount >= 0, "number of rules cannot be negative");

        } catch (Exception e) {
            logger.error(" !!! Connection error to PostgreSQL: {}", e.getMessage());
            fail("PostgreSQL NOT CONNECTED: " + e.getMessage());
        }
    }

    @Test
    void testLiquibaseMigrationsApplied() {
        logger.info("=== CHECKING THE APPLICATION OF MIGRATIONS LIQUIBASE ===");

        try {

            long rulesCount = dynamicRuleRepository.count();

            logger.info(" Liquibase migrations applied successfully");
            logger.info("Tables have been created, queries can be performed");

        } catch (Exception e) {
            logger.error(" !!! Error checking migrations: {}", e.getMessage());
            fail("Liquibase migrations are not applied: " + e.getMessage());
        }
    }
}
