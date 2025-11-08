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
import java.util.Properties;

/**
 * Конфигурация для загрузки переменных окружения из .env файла.
 * Поддерживает два способа загрузки:
 * 1. Из корневой директории проекта (для разработки)
 * 2. Из classpath (для продакшена)
 * Переменные из .env имеют приоритет над application.properties.
 */
@Configuration
public class EnvConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(EnvConfiguration.class);

    private static final String ENV_FILE = "configuration.env";
    private static final String ENV_DEV_FILE = "configuration.env.dev";

    private final Environment environment;
    public EnvConfiguration(Environment environment) {
        this.environment = environment;
    }


    /**
     * Инициализация переменных окружения из configuration.env файла.
     * Загружает сначала общие настройки из configuration.env,
     * затем переопределяет настройками из профильного файла (например, configuration.env.dev).
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

        envProperties.forEach((key, value) -> {
            String keyStr = (String) key;
            String valueStr = (String) value;

            if (System.getProperty(keyStr) == null) {
                System.setProperty(keyStr, valueStr);

            }
        });

        logger.info("Loaded {} variables from configuration.env files", envProperties.size());
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
