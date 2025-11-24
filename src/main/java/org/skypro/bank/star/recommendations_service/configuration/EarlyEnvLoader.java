package org.skypro.bank.star.recommendations_service.configuration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;

import java.util.Arrays;
import java.util.Properties;

@Component
public class EarlyEnvLoader implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment,
                                       SpringApplication application) {
        System.out.println(" === РАННЯЯ ЗАГРУЗКА .env ФАЙЛОВ ===");

        loadEnvFile("configuration.env", environment);

        String[] activeProfiles = environment.getActiveProfiles();
        boolean isDevActive = Arrays.stream(activeProfiles)
                .anyMatch(profile -> profile.equals("dev"));

        // Загружаем dev конфиг ТОЛЬКО если явно указан профиль dev
        if (isDevActive) {
            loadEnvFile("configuration.env.dev", environment);
            System.out.println("Dev profile active - loaded configuration.env.dev");
        }
    }

    private void loadEnvFile(String filename, ConfigurableEnvironment environment) {
        try {
            FileSystemResource resource = new FileSystemResource(filename);
            if (resource.exists()) {
                Properties properties = PropertiesLoaderUtils.loadProperties(resource);
                environment.getPropertySources()
                        .addFirst(new PropertiesPropertySource(filename, properties));


                properties.forEach((key, value) -> {
                    String keyStr = (String) key;
                    String valueStr = (String) value;
                    if (System.getProperty(keyStr) == null) {
                        System.setProperty(keyStr, valueStr);
                    }
                });

                System.out.println(" Загружен: " + filename + " (" + properties.size() + " свойств)");
            } else {
                System.out.println(" Файл не найден: " + filename);
            }
        } catch (IOException e) {
            System.out.println(" Ошибка загрузки " + filename + ": " + e.getMessage());
        }
    }
}
