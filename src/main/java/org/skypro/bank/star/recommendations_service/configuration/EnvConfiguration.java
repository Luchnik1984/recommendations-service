package org.skypro.bank.star.recommendations_service.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Properties;

/**
 * Конфигурация для загрузки переменных окружения из .env файла.
 * Поддерживает два способа загрузки:
 * 1. Из корневой директории проекта (для разработки)
 * 2. Из classpath (для продакшена)
 * 3. Автоматические fallback-значения для тестового окружения
 * Переменные из .env имеют приоритет над application.properties.
 * Для тестового профиля автоматически настраиваются H2 in-memory базы данных.
 */
@Configuration
public class EnvConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(EnvConfiguration.class);

    private static final String ENV_FILE = "configuration.env";
    private static final String ENV_DEV_FILE = "configuration.env.dev";
    private static final String ENV_TEST_FILE = "configuration.env.test";

    private final Environment environment;
    public EnvConfiguration(Environment environment) {
        this.environment = environment;
    }


    /**
     * Инициализация переменных окружения из configuration.env файла.
     * Загружает сначала общие настройки из configuration.env,
     * затем переопределяет настройками из профильного файла (например, configuration.env.dev).
     *  Для тестовых профилей автоматически устанавливает fallback-значения
     *  для H2 in-memory баз данных
     */
    @PostConstruct
    public void loadEnvVariables() {
        Properties envProperties = new Properties();

        loadEnvFile(ENV_FILE, envProperties);


        String[] activeProfiles = environment.getActiveProfiles();
        if (activeProfiles.length > 0) {
            for (String profile : activeProfiles) {
                String profileEnvFile = "configuration.env." + profile;
                loadEnvFile(profileEnvFile, envProperties);
            }
        } else {
            loadEnvFile(ENV_DEV_FILE, envProperties);
        }
        // Для тестов добавляем fallback на H2 in-memory
        if (isTestProfileActive(activeProfiles)) {
            setupTestFallbacks(envProperties);
            logger.info("Test profile detected - configured H2 in-memory databases");
        }

        envProperties.forEach((key, value) -> {
            String keyStr = (String) key;
            String valueStr = (String) value;

            if (System.getProperty(keyStr) == null) {
                System.setProperty(keyStr, valueStr);

            }
        });

        logger.info("Loaded {} variables from configuration.env files for profiles: {}",
                envProperties.size(), Arrays.toString(activeProfiles));
    }

    /**
     * Проверяет, активен ли тестовый профиль.
     * Тестовыми считаются профили 'test'.
     *
     * @param activeProfiles массив активных Spring профилей
     * @return true если активен тестовый профиль, false в противном случае
     */
    private boolean isTestProfileActive(String[] activeProfiles) {
        return Arrays.stream(activeProfiles)
                .anyMatch(profile -> profile.equals("test"));
    }

    /**
     * Устанавливает fallback-значения для тестового окружения.
     * Обеспечивает работу тестов с H2 in-memory базами данных даже при отсутствии
     * configuration.env.test файла.
     *
     * @param properties объект Properties для установки значений по умолчанию
     */
    private void setupTestFallbacks(Properties properties) {
        // Fallback для основной H2 in-memory базы (транзакционные данные)
        properties.putIfAbsent("H2_DATABASE_URL",
                "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");

        // Fallback для H2 in-memory базы динамических правил (эмулирует PostgreSQL)
        properties.putIfAbsent("POSTGRES_URL",
                "jdbc:h2:mem:dynamic_rules_test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=PostgreSQL");
        properties.putIfAbsent("POSTGRES_USERNAME", "sa");
        properties.putIfAbsent("POSTGRES_PASSWORD", "");

        // Fallback для порта и уровня логирования
        properties.putIfAbsent("SERVER_PORT", "8080");
        properties.putIfAbsent("LOGGING_LEVEL", "DEBUG");
    }


    /**
     * Загружает configuration.env файл и добавляет свойства в указанный Properties объект.
     * @param envFileName имя .env файла
     * @param properties объект Properties для заполнения
     */
    private void loadEnvFile(String envFileName, Properties properties) {
        Path envPath = Paths.get(envFileName);

        if (!Files.exists(envPath)) {
            logger.debug("File {} not found", envFileName);
            return;
        }

        try {
            Properties fileProperties = new Properties();
            fileProperties.load(Files.newBufferedReader(envPath));
            properties.putAll(fileProperties);
            logger.info("The file was uploaded successfully: {}", envFileName);

        } catch (IOException e) {
            logger.warn("File reading error {}: {}", envFileName, e.getMessage());
        }
    }
}
