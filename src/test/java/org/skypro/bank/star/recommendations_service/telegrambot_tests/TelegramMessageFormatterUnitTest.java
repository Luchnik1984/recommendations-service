package org.skypro.bank.star.recommendations_service.telegrambot_tests;

import org.junit.jupiter.api.Test;
import org.skypro.bank.star.recommendations_service.model.dto.RecommendationDTO;
import org.skypro.bank.star.recommendations_service.model.dto.RecommendationResponse;
import org.skypro.bank.star.recommendations_service.model.dto.UserInfoDto;
import org.skypro.bank.star.recommendations_service.telegram.formater.TelegramMessageFormatter;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class TelegramMessageFormatterUnitTest {

    private final TelegramMessageFormatter formatter = new TelegramMessageFormatter();

    @Test
    void formatWelcomeMessage_ShouldFormatCorrectly() {

        UserInfoDto user = new UserInfoDto(
                UUID.fromString("00000000-0000-0000-0000-000000000001"),
                "test.user",
                "Иван",
                "Тестов"
        );

        String result = formatter.formatWelcomeMessage(user);

        assertThat(result).isEqualTo("👤 <b>Здравствуйте, Иван Тестов!</b>");
    }

    @Test
    void formatRecommendations_WithEmptyList_ShouldReturnNoProductsMessage() {

        String result = formatter.formatRecommendations(List.of());

        assertThat(result).contains("К сожалению, для вас нет подходящих продуктов");
    }

    @Test
    void formatRecommendations_WithMultipleProducts_ShouldFormatList() {

        List<RecommendationDTO> recommendations = Arrays.asList(
                new RecommendationDTO(
                        UUID.fromString("11111111-1111-1111-1111-111111111111"),
                        "Инвестиционный портфель",
                        "Создайте диверсифицированный инвестиционный портфель"
                ),
                new RecommendationDTO(
                        UUID.fromString("22222222-2222-2222-2222-222222222222"),
                        "Премиальная карта",
                        "Получите премиальную карту с повышенным кэшбэком"
                )
        );


        String result = formatter.formatRecommendations(recommendations);

        assertThat(result).contains("Новые продукты для вас");
        assertThat(result).contains("1. Инвестиционный портфель");
        assertThat(result).contains("2. Премиальная карта");
        assertThat(result).contains("━━━━━━━━━━━━━━━━━━━━");
    }

    @Test
    void formatFullMessage_ShouldCombineWelcomeAndRecommendations() {

        UserInfoDto user = new UserInfoDto(
                UUID.fromString("00000000-0000-0000-0000-000000000001"),
                "test.user",
                "Иван",
                "Тестов"
        );

        List<RecommendationDTO> recommendations = List.of(
                new RecommendationDTO(
                        UUID.fromString("11111111-1111-1111-1111-111111111111"),
                        "Тестовый продукт",
                        "Описание тестового продукта"
                )
        );

        RecommendationResponse response = new RecommendationResponse(user.id(), recommendations);


        String result = formatter.formatFullMessage(user, response);

        assertThat(result).contains("Здравствуйте, Иван Тестов");
        assertThat(result).contains("Новые продукты для вас");
        assertThat(result).contains("Тестовый продукт");
    }

    @Test
    void formatHelpMessage_ShouldContainAllCommands() {

        String result = formatter.formatHelpMessage();

        assertThat(result).contains("Банк \"Стар\"");
        assertThat(result).contains("/start");
        assertThat(result).contains("/recommend");
        assertThat(result).contains("Доступные команды");
    }
}

