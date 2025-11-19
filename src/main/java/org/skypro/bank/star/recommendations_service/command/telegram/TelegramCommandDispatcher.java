package org.skypro.bank.star.recommendations_service.command.telegram;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Optional;

/**
 * Диспетчер для обработки команд Telegram бота.
 * Находит подходящий обработчик для входящего сообщения.
 *
 * @see TelegramCommand
 */

@Component
public class TelegramCommandDispatcher {

    private static final Logger logger = LoggerFactory.getLogger(TelegramCommandDispatcher.class);

    private final List<TelegramCommand> commands;

    /**
     * Конструктор диспетчера команд.
     *
     * @param commands список всех доступных команд (внедряется Spring)
     */
    public TelegramCommandDispatcher(List<TelegramCommand> commands) {
        this.commands = commands;
        logger.info("TelegramCommandDispatcher initialized with {} commands", commands.size());
    }

    /**
     * Находит подходящий обработчик для сообщения.
     *
     * @param messageText текст сообщения
     * @return Optional с найденным обработчиком или empty если не найден
     */
    public Optional<TelegramCommand> findHandler(String messageText) {
        return commands.stream()
                .filter(command -> command.canHandle(messageText))
                .findFirst();
    }

    /**
     * Обрабатывает сообщение с помощью подходящего обработчика.
     *
     * @param update объект с данными сообщения
     * @param chatId идентификатор чата
     * @return текст ответа для отправки пользователю
     */
    public String processMessage(Update update, Long chatId) {
        String messageText = update.getMessage().getText();

        Optional<TelegramCommand> handler = findHandler(messageText);
        if (handler.isPresent()) {
            TelegramCommand command = handler.get();
            logger.debug("Found handler for message: {} -> {}", messageText, command.getClass().getSimpleName());
            return command.handle(update, chatId);
        }

        logger.debug("No handler found for message: {}", messageText);
        return null;
    }

}
