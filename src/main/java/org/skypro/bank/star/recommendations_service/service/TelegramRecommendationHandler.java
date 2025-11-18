package org.skypro.bank.star.recommendations_service.service;

import org.skypro.bank.star.recommendations_service.command.telegram.TelegramCommand;
import org.skypro.bank.star.recommendations_service.command.telegram.TelegramCommandDispatcher;
import org.skypro.bank.star.recommendations_service.model.dto.UserSearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Обработчик команд Telegram бота для выдачи рекомендаций банковских продуктов.
 * Использует TelegramCommandDispatcher для делегирования обработки конкретных команд.
 * Отвечает за взаимодействие с Telegram API и отправку сообщений.
 *
 * @see TelegramCommandDispatcher
 * @see TelegramCommand
 */
@Component
public class TelegramRecommendationHandler extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(TelegramRecommendationHandler.class);

    private final String botUsername;
    private final TelegramCommandDispatcher commandDispatcher;

    /**
     * Конструктор обработчика Telegram бота.
     *
     * @param botUsername имя бота в Telegram
     * @param botToken токен бота для аутентификации
     * @param commandDispatcher диспетчер команд для обработки сообщений

     */
    public TelegramRecommendationHandler(
            @Value("${TELEGRAM_BOT_USERNAME:bank_star_recommendations_bot}") String botUsername,
            @Value("${TELEGRAM_BOT_TOKEN:}") String botToken,
            TelegramCommandDispatcher commandDispatcher) {

        super(botToken);
        this.botUsername = botUsername;
        this.commandDispatcher = commandDispatcher;

    }

    /**
     * Возвращает имя бота в Telegram.
     *
     * @return имя бота
     */
    @Override
    public String getBotUsername() {
        return botUsername;
    }


    /**
     * Основной метод обработки входящих сообщений от пользователей.
     *Делегирует обработку командам через TelegramCommandDispatcher.
     *
     * @param update объект с данными входящего сообщения
     */
    @Override
    public void onUpdateReceived(Update update) {
        logger.debug("Received update: {}", update);

        if (!update.hasMessage() || !update.getMessage().hasText()) {
            logger.debug("Ignoring non-text update");
            return;
        }

        String messageText = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();

        logger.info("Processing message from chat {}: '{}'", chatId, messageText);

        try {
            processMessage(chatId, update);
        } catch (Exception e) {
            logger.error("Error processing message from chat {}: {}", chatId, e.getMessage(), e);
            sendErrorMessage(chatId, "Произошла ошибка при обработке запроса. Попробуйте позже.");
        }
    }

    /**
     * Обрабатывает сообщение с помощью диспетчера команд.
     *
     * @param chatId идентификатор чата
     * @param update объект с данными сообщения
     */
    private void processMessage(Long chatId, Update update) {
        String response = commandDispatcher.processMessage(update, chatId);
        if (response != null) {
            sendFormattedMessage(chatId, response);
        } else {
            logger.warn("No response generated for message from chat: {}", chatId);
            sendErrorMessage(chatId, "Не удалось обработать команду.");
        }
    }

    /**
     * Отправляет сообщение об ошибке пользователю.
     *
     * @param chatId идентификатор чата
     * @param errorMessage текст сообщения об ошибке
     */
    private void sendErrorMessage(Long chatId, String errorMessage) {
        // Используем простой формат для ошибок, так как форматтер может быть недоступен
        String message = "❌ <b>Ошибка:</b> " + escapeHtml(errorMessage);
        sendFormattedMessage(chatId, message);
    }

    /**
     * Отправляет форматированное текстовое сообщение в указанный чат.
     *
     * @param chatId идентификатор чата
     * @param text текст сообщения с HTML разметкой
     */
    private void sendFormattedMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        message.setParseMode("HTML");

        try {
            execute(message);
            logger.debug("Formatted message sent to chat {}: {}", chatId, text);
        } catch (TelegramApiException e) {
            logger.error("Failed to send formatted message to chat {}: {}", chatId, e.getMessage(), e);
            sendPlainMessage(chatId, removeHtmlTags(text));
        }
    }

    /**
     * Отправляет обычное текстовое сообщение (без форматирования).
     *
     * @param chatId идентификатор чата
     * @param text текст сообщения
     */
    private void sendPlainMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);

        try {
            execute(message);
            logger.debug("Plain message sent to chat {}: {}", chatId, text);
        } catch (TelegramApiException e) {
            logger.error("Failed to send plain message to chat {}: {}", chatId, e.getMessage(), e);
        }
    }

    /**
     * Удаляет HTML теги из текста для fallback сообщений.
     *
     * @param htmlText текст с HTML разметкой
     * @return чистый текст без HTML тегов
     */
    private String removeHtmlTags(String htmlText) {
        return htmlText.replaceAll("<[^>]*>", "");
    }

    /**
     * Экранирует специальные HTML символы.
     *
     * @param text текст для экранирования
     * @return экранированный текст
     */
    private String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

}
