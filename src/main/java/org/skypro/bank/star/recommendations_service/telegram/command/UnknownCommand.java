package org.skypro.bank.star.recommendations_service.telegram.command;

import org.skypro.bank.star.recommendations_service.telegram.formater.TelegramMessageFormatter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Обработчик неизвестных команд для Telegram бота.
 * Вызывается когда другие обработчики не подходят.
 */
@Component
public class UnknownCommand implements TelegramCommand {

    private final TelegramMessageFormatter messageFormatter;

    public UnknownCommand(TelegramMessageFormatter messageFormatter) {
        this.messageFormatter = messageFormatter;
    }

    @Override
    public boolean canHandle(String messageText) {
        return true;
    }

    @Override
    public String handle(Update update, Long chatId) {
        return messageFormatter.formatHelpMessage();
    }

    @Override
    public String getDescription() {
        return "Неизвестная команда - показывает справку";
    }
}
