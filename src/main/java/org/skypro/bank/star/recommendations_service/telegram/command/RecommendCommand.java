package org.skypro.bank.star.recommendations_service.telegram.command;

import org.skypro.bank.star.recommendations_service.telegram.TelegramBotFacade;
import org.skypro.bank.star.recommendations_service.telegram.formater.TelegramMessageFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Обработчик команды /recommend для Telegram бота.
 */
@Component
public class RecommendCommand implements TelegramCommand {

    private static final Logger logger = LoggerFactory.getLogger(RecommendCommand.class);

    private final TelegramBotFacade telegramBotFacade;
    private final TelegramMessageFormatter messageFormatter;

    public RecommendCommand(TelegramBotFacade telegramBotFacade, TelegramMessageFormatter messageFormatter) {
        this.telegramBotFacade = telegramBotFacade;
        this.messageFormatter = messageFormatter;
        logger.debug("RecommendCommand initialized");
    }

    @Override
    public boolean canHandle(String messageText) {
        return messageText != null && messageText.startsWith("/recommend");
    }

    @Override
    public String handle(Update update, Long chatId) {
        String messageText = update.getMessage().getText();
        String[] parts = messageText.split("\\s+", 2);


        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            logger.warn("Empty username parameter in /recommend command from chat: {}", chatId);
            return "❌ <b>Пожалуйста, укажите username пользователя.</b>\n\n<i>Пример: /recommend sheron.berge</i>";
        }

        String username = parts[1].trim();
        logger.info("Processing /recommend command for username: '{}' in chat: {}", username, chatId);

        try {

            TelegramBotFacade.BotSearchResult result = telegramBotFacade.findUserAndRecommendationsByUsername(username);

            logger.debug("Command result type: {} for username: '{}'", result.getResultType(), username);


            switch (result.getResultType()) {
                case USER_NOT_FOUND:
                    logger.info("Sending 'user not found' response for username: '{}' to chat: {}", username, chatId);
                    return telegramBotFacade.getFormattedUserNotFoundMessage(username);

                case SUCCESS:
                    logger.info("Sending recommendations for user: {} to chat: {}",
                            result.getUser().getFullName(), chatId);
                    return telegramBotFacade.getFormattedMessage(result.getUser(), result.getRecommendations());

                case ERROR:
                    logger.error("Error result for username: '{}': {}", username, result.getMessage());
                    return messageFormatter.formatErrorMessage(result.getMessage());

                default:
                    logger.error("Unknown result type: {} for username: '{}'", result.getResultType(), username);
                    return "❌ <b>Ошибка:</b> Неизвестный тип результата";
            }

        } catch (Exception e) {
            logger.error("Unexpected error in /recommend command for username '{}' in chat {}: {}",
                    username, chatId, e.getMessage(), e);
            /* ГАРАНТИРУЕМ возврат String даже при ошибке */
            return "❌ <b>Ошибка:</b> Непредвиденная ошибка при обработке запроса.";
        }
    }

    @Override
    public String getDescription() {
        return "/recommend [username] - получить персонализированные рекомендации";
    }

}
