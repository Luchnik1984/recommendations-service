package org.skypro.bank.star.recommendations_service.configuration;

import org.skypro.bank.star.recommendations_service.telegram.TelegramRecommendationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * Конфигурация для Telegram бота рекомендательной системы банка "Стар".
 */
@Configuration
public class TelegramBotConfig {

    private static final Logger logger = LoggerFactory.getLogger(TelegramBotConfig.class);

    /**
     * Создает и регистрирует Telegram бота в Spring контексте.
     * TelegramRecommendationHandler загружает конфигурацию через @Value.
     *
     * @param telegramRecommendationHandler обработчик команд бота
     * @return TelegramBotsApi для управления ботами
     * @throws TelegramApiException если произошла ошибка при регистрации бота
     */
    @Bean
    public TelegramBotsApi telegramBotsApi(TelegramRecommendationHandler telegramRecommendationHandler)
            throws TelegramApiException {

        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);

        try {
            botsApi.registerBot(telegramRecommendationHandler);
            logger.info("Telegram bot successfully registered with username: {}",
                    telegramRecommendationHandler.getBotUsername());
        } catch (TelegramApiException e) {
            logger.error("Failed to register Telegram bot: {}", e.getMessage());
            throw e;
        }

        return botsApi;
    }

}
