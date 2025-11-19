package org.skypro.bank.star.recommendations_service.telegrambot_tests;

import org.junit.jupiter.api.Test;
import org.skypro.bank.star.recommendations_service.service.TelegramBotFacade;
import org.skypro.bank.star.recommendations_service.enums.BotSearchResultType; // Добавляем импорт
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class TelegramBotFacadeIntegrationTest {

    @Autowired
    private TelegramBotFacade telegramBotFacade;

    @Test
    void findUserAndRecommendationsByUsername_WithValidUser_ShouldReturnSuccess() {

        var result = telegramBotFacade.findUserAndRecommendationsByUsername("invest.user");

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getUser()).isNotNull();
        assertThat(result.getUser().username()).isEqualTo("invest.user");
        assertThat(result.getRecommendations()).isNotNull();
        assertThat(result.getResultType()).isEqualTo(BotSearchResultType.SUCCESS);
    }

    @Test
    void findUserAndRecommendationsByUsername_WithNonExistentUser_ShouldReturnUserNotFound() {

        var result = telegramBotFacade.findUserAndRecommendationsByUsername("nonexistent.user");

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).isEqualTo("Пользователь не найден");
        assertThat(result.getUser()).isNull();
        assertThat(result.getRecommendations()).isNull();
        assertThat(result.getResultType()).isEqualTo(BotSearchResultType.USER_NOT_FOUND);
    }

    @Test
    void getFormattedMessage_WithUserAndRecommendations_ShouldReturnFormattedMessage() {

        var searchResult = telegramBotFacade.findUserAndRecommendationsByUsername("invest.user");
        var user = searchResult.getUser();
        var recommendations = searchResult.getRecommendations();

        String formattedMessage = telegramBotFacade.getFormattedMessage(user, recommendations);

        assertThat(formattedMessage).isNotNull();
        assertThat(formattedMessage).contains("Здравствуйте, Алексей Инвесторов");
        assertThat(formattedMessage).contains("Новые продукты для вас");
    }

    @Test
    void getFormattedUserNotFoundMessage_ShouldReturnProperMessage() {

        String message = telegramBotFacade.getFormattedUserNotFoundMessage("test.user");

        assertThat(message).isNotNull();
        assertThat(message).contains("Пользователь не найден");
        assertThat(message).contains("test.user");
    }

    @Test
    void telegramBotFacade_ShouldHandleDifferentUserScenarios() {

        String[] testUsers = {"saving.user", "credit.user", "basic.user", "active.user"};

        for (String username : testUsers) {
            var result = telegramBotFacade.findUserAndRecommendationsByUsername(username);
            assertThat(result.isSuccess()).isTrue();
            assertThat(result.getUser()).isNotNull();
            assertThat(result.getUser().username()).isEqualTo(username);
        }
    }
}
