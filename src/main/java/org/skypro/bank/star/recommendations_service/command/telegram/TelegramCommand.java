package org.skypro.bank.star.recommendations_service.command.telegram;

import org.skypro.bank.star.recommendations_service.service.TelegramRecommendationHandler;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Интерфейс для обработчиков команд Telegram бота.
 * Реализует паттерн Команда для обработки различных типов сообщений.
 *
 * @see TelegramCommandDispatcher
 * @see TelegramRecommendationHandler
 */
public interface TelegramCommand {

    /**
     * Проверяет, может ли данный обработчик обработать сообщение.
     *
     * @param messageText текст сообщения от пользователя
     * @return true если обработчик может обработать это сообщение
     */
    boolean canHandle(String messageText);

    /**
     * Обрабатывает сообщение и возвращает ответ.
     *
     * @param update объект с данными сообщения
     * @param chatId идентификатор чата
     * @return текст ответа для отправки пользователю
     */
    String handle(Update update, Long chatId);

    /**
     * Возвращает описание команды для справки.
     *
     * @return описание команды
     */
    String getDescription();
}
