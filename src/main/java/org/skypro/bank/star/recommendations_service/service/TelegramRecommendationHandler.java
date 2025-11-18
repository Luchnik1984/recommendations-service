package org.skypro.bank.star.recommendations_service.service;

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
 * Реализует основную логику взаимодействия с пользователями через Telegram.
 * Поддерживает команды /start и /recommend согласно требованиям.
 *
 * @see UserSearchService
 * @see RecommendationService
 */
@Component
public class TelegramRecommendationHandler extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(TelegramRecommendationHandler.class);

    private final String botUsername;
    private final UserSearchService userSearchService;

    /**
     * Конструктор обработчика Telegram бота.
     * Токен передается в родительский конструктор TelegramLongPollingBot.
     *
     * @param botUsername имя бота в Telegram
     * @param botToken токен бота для аутентификации
     * @param userSearchService сервис поиска пользователей
     */
    public TelegramRecommendationHandler(
            @Value("${TELEGRAM_BOT_USERNAME:bank_star_recommendations_bot}") String botUsername,
            @Value("${TELEGRAM_BOT_TOKEN:}") String botToken,
            UserSearchService userSearchService) {

        super(botToken);
        this.botUsername = botUsername;
        this.userSearchService = userSearchService;

        logger.debug("Telegram bot handler created with username: {}", botUsername);
    }

    /**
     * Возвращает имя бота в Telegram.
     * Используется Telegram API для идентификации бота.
     *
     * @return имя бота
     */
    @Override
    public String getBotUsername() {
        return botUsername;
    }


    /**
     * Основной метод обработки входящих сообщений от пользователей.
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
            processMessage(chatId, messageText);
        } catch (Exception e) {
            logger.error("Error processing message from chat {}: {}", chatId, e.getMessage(), e);
            sendErrorMessage(chatId, "Произошла ошибка при обработке запроса. Попробуйте позже.");
        }
    }

    /**
     * Обрабатывает текстовое сообщение от пользователя.
     *
     * @param chatId идентификатор чата
     * @param messageText текст сообщения
     */
    private void processMessage(Long chatId, String messageText) {
        if (messageText.startsWith("/start")) {
            handleStartCommand(chatId);
        } else if (messageText.startsWith("/recommend")) {
            handleRecommendCommand(chatId, messageText);
        } else {
            handleUnknownCommand(chatId);
        }
    }

    /**
     * Обрабатывает команду /start - приветствие и справка.
     *
     * @param chatId идентификатор чата
     */
    private void handleStartCommand(Long chatId) {
        String welcomeMessage = """
                🏦 <b>Добро пожаловать в банк "Стар"!</b>
                
                Я - ваш помощник по подбору банковских продуктов.
                
                <b>Доступные команды:</b>
                /start - показать это сообщение
                /recommend [имя] - получить персонализированные рекомендации
                
                <i>Пример: /recommend Иван</i>
                """;

        sendFormattedMessage(chatId, welcomeMessage);
        logger.info("Sent welcome message to chat: {}", chatId);
    }

    /**
     * Обрабатывает команду /recommend - поиск рекомендаций по имени пользователя.
     * Согласно требованиям, возвращает "Пользователь не найден" для случаев:
     * - NOT_FOUND (пользователь не найден)
     * - MULTIPLE_USERS_FOUND (найдено несколько пользователей)
     *
     * @param chatId идентификатор чата
     * @param messageText полный текст команды
     */
    private void handleRecommendCommand(Long chatId, String messageText) {
        String[] parts = messageText.split("\\s+", 2);
        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            sendFormattedMessage(chatId,
                    "❌ <b>Пожалуйста, укажите имя пользователя.</b>\n\n<i>Пример: /recommend Иван</i>");
            return;
        }

        String searchName = parts[1].trim();
        logger.info("Searching recommendations for: '{}' in chat: {}", searchName, chatId);

        try {
            UserSearchResult searchResult = userSearchService.searchUser(searchName);

            switch (searchResult.searchStatus()) {
                case NOT_FOUND:
                    sendUserNotFoundMessage(chatId, searchName);
                    break;
                case MULTIPLE_USERS_FOUND:
                    // Согласно требованиям: "Если найдено несколько пользователей, бот выдает сообщение «Пользователь не найден»"
                    sendUserNotFoundMessage(chatId, searchName);
                    logger.debug("Multiple users found for '{}', returning 'not found' as required", searchName);
                    break;
                case SINGLE_USER_FOUND:
                    handleSingleUserFound(chatId, searchResult);
                    break;
            }
        } catch (Exception e) {
            logger.error("Error searching user '{}': {}", searchName, e.getMessage(), e);
            sendErrorMessage(chatId, "Ошибка при поиске пользователя в системе банка.");
        }
    }

    /**
     * Обрабатывает случай, когда найден ровно один пользователь.
     * Показывает информацию о пользователе (будет расширено в US15 с рекомендациями).
     *
     * @param chatId идентификатор чата
     * @param searchResult результат поиска с одним пользователем
     */
    private void handleSingleUserFound(Long chatId, UserSearchResult searchResult) {
        String userFullName = searchResult.foundUsers().get(0).getFullName();

        String message = """
                ✅ <b>Пользователь найден!</b>
                
                👤 <b>Здравствуйте, %s!</b>
                
                🔍 <i>Поиск рекомендаций...</i>
                
                <i>Функция вывода рекомендаций будет доступна в следующем обновлении.</i>
                """.formatted(userFullName);

        sendFormattedMessage(chatId, message);
        logger.info("Single user found: {} in chat: {}", userFullName, chatId);
    }

    /**
     * Отправляет сообщение "Пользователь не найден" согласно требованиям.
     * Используется для случаев NOT_FOUND и MULTIPLE_USERS_FOUND.
     *
     * @param chatId идентификатор чата
     * @param searchName имя, по которому выполнялся поиск
     */
    private void sendUserNotFoundMessage(Long chatId, String searchName) {
        String message = """
                ❌ <b>Пользователь не найден</b>
                
                По запросу <i>"%s"</i> пользователь не найден в системе банка.
                
                💡 <i>Проверьте правильность написания имени и фамилии.</i>
                """.formatted(searchName);

        sendFormattedMessage(chatId, message);
        logger.info("User not found for search: '{}' in chat: {}", searchName, chatId);
    }

    /**
     * Обрабатывает неизвестные команды.
     *
     * @param chatId идентификатор чата
     */
    private void handleUnknownCommand(Long chatId) {
        String helpMessage = """
                🤔 <b>Неизвестная команда</b>
                
                <b>Доступные команды:</b>
                /start - показать справку
                /recommend [имя] - получить рекомендации
                
                <i>Пример: /recommend Иван</i>
                """;

        sendFormattedMessage(chatId, helpMessage);
    }

    /**
     * Отправляет сообщение об ошибке пользователю.
     *
     * @param chatId идентификатор чата
     * @param errorMessage текст сообщения об ошибке
     */
    private void sendErrorMessage(Long chatId, String errorMessage) {
        String message = "❌ <b>Ошибка:</b> " + errorMessage;
        sendFormattedMessage(chatId, message);
    }

    /**
     * Отправляет форматированное текстовое сообщение в указанный чат.
     * Использует HTML разметку для красивого оформления.
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
     * Используется как fallback при ошибках форматирования.
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
}
