package org.skypro.bank.star.recommendations_service.telegram.command;

import org.skypro.bank.star.recommendations_service.telegram.formater.TelegramMessageFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Обработчик команды /start для Telegram бота.
 */
@Component
public class StartCommand implements TelegramCommand {

    private static final Logger logger = LoggerFactory.getLogger(StartCommand.class);

    private final TelegramMessageFormatter messageFormatter;

    public StartCommand(TelegramMessageFormatter messageFormatter) {
        this.messageFormatter = messageFormatter;
    }

    @Override
    public boolean canHandle(String messageText) {
        return messageText.startsWith("/start");
    }

    @Override
    public String handle(Update update, Long chatId) {
        logger.info("Processing /start command from chat: {}", chatId);
        return messageFormatter.formatHelpMessage();
    }

    @Override
    public String getDescription() {
        return "/start - показать справку";
    }
}
