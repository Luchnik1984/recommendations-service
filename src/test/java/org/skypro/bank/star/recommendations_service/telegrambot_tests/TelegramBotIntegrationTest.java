package org.skypro.bank.star.recommendations_service.telegrambot_tests;

import org.junit.jupiter.api.Test;
import org.skypro.bank.star.recommendations_service.command.telegram.TelegramCommandDispatcher;
import org.skypro.bank.star.recommendations_service.service.TelegramBotFacade;
import org.skypro.bank.star.recommendations_service.service.TelegramMessageFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class TelegramBotIntegrationTest {

    @Autowired
    private TelegramCommandDispatcher commandDispatcher;

    @Autowired
    private TelegramMessageFormatter messageFormatter;

    @Autowired
    private TelegramBotFacade telegramBotFacade;

    @Test
    void telegramCommandDispatcher_ShouldHandleStartCommand() {

        String messageText = "/start";
        Long chatId = 123456789L;

        String response = commandDispatcher.processMessage(createUpdate(messageText, chatId), chatId);

        assertThat(response).isNotNull();
        assertThat(response).contains("Банк \"Стар\"");
        assertThat(response).contains("Доступные команды");
    }

    @Test
    void telegramCommandDispatcher_ShouldHandleRecommendCommand() {

        String messageText = "/recommend invest.user";
        Long chatId = 123456789L;

        String response = commandDispatcher.processMessage(createUpdate(messageText, chatId), chatId);

        assertThat(response).isNotNull();

        assertThat(response).contains("Здравствуйте");
    }

    @Test
    void telegramCommandDispatcher_ShouldHandleUnknownCommand() {
        String messageText = "/unknown";
        Long chatId = 123456789L;

        String response = commandDispatcher.processMessage(createUpdate(messageText, chatId), chatId);

        assertThat(response).isNotNull();

        assertThat(response).contains("Банк \"Стар\"");
        assertThat(response).contains("Доступные команды");
        assertThat(response).contains("/start");
        assertThat(response).contains("/recommend");
        assertThat(response).contains("Пример: /recommend sheron.berge");
    }

    @Test
    void telegramMessageFormatter_ShouldFormatWelcomeMessage() {

        var user = telegramBotFacade.findUserAndRecommendationsByUsername("invest.user").getUser();

        String welcomeMessage = messageFormatter.formatWelcomeMessage(user);

        assertThat(welcomeMessage).isEqualTo("👤 <b>Здравствуйте, Алексей Инвесторов!</b>");
    }

    @Test
    void telegramMessageFormatter_ShouldFormatUserNotFoundMessage() {

        String message = messageFormatter.formatUserNotFoundMessage("nonexistent.user");


        assertThat(message).contains("Пользователь не найден");
        assertThat(message).contains("nonexistent.user");
    }

    @Test
    void telegramBotFacade_ShouldFindUserAndGenerateRecommendations() {

        var result = telegramBotFacade.findUserAndRecommendationsByUsername("invest.user");

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getUser()).isNotNull();
        assertThat(result.getUser().getFullName()).isEqualTo("Алексей Инвесторов");
        assertThat(result.getRecommendations()).isNotNull();
    }

    @Test
    void telegramBotFacade_ShouldHandleUserNotFound() {

        var result = telegramBotFacade.findUserAndRecommendationsByUsername("nonexistent.user");

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).isEqualTo("Пользователь не найден");
    }

    private org.telegram.telegrambots.meta.api.objects.Update createUpdate(String text, Long chatId) {
        org.telegram.telegrambots.meta.api.objects.Update update = new org.telegram.telegrambots.meta.api.objects.Update();
        org.telegram.telegrambots.meta.api.objects.Message message = new org.telegram.telegrambots.meta.api.objects.Message();
        message.setText(text);

        org.telegram.telegrambots.meta.api.objects.Chat chat = new org.telegram.telegrambots.meta.api.objects.Chat();
        chat.setId(chatId);
        message.setChat(chat);

        update.setMessage(message);
        return update;
    }


    @Test
    void startCommand_ShouldReturnHelpMessage() {

        String messageText = "/start";
        Long chatId = 123456789L;

        String response = commandDispatcher.processMessage(createUpdate(messageText, chatId), chatId);

        assertThat(response).isNotNull();
        assertThat(response).contains("Банк \"Стар\"");
        assertThat(response).contains("Доступные команды");
        assertThat(response).contains("/start");
        assertThat(response).contains("/recommend");
    }

    @Test
    void recommendCommand_WithUsername_ShouldReturnRecommendations() {

        String messageText = "/recommend invest.user";
        Long chatId = 123456789L;

        String response = commandDispatcher.processMessage(createUpdate(messageText, chatId), chatId);

        assertThat(response).isNotNull();
        assertThat(response).contains("Здравствуйте, Алексей Инвесторов");
        assertThat(response).contains("Новые продукты для вас");
    }

    @Test
    void recommendCommand_WithoutUsername_ShouldReturnHelpMessage() {

        String messageText = "/recommend";
        Long chatId = 123456789L;

        String response = commandDispatcher.processMessage(createUpdate(messageText, chatId), chatId);

        assertThat(response).isNotNull();

        boolean isHelpMessage = response.contains("Банк \"Стар\"") && response.contains("Доступные команды");
        boolean isErrorMessage = response.contains("username") || response.contains("Имя пользователя");

        assertThat(isHelpMessage || isErrorMessage).isTrue();
    }
}
