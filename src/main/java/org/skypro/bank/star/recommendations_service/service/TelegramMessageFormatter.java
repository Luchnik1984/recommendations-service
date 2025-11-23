package org.skypro.bank.star.recommendations_service.service;

import org.skypro.bank.star.recommendations_service.model.dto.RecommendationDTO;
import org.skypro.bank.star.recommendations_service.model.dto.RecommendationResponse;
import org.skypro.bank.star.recommendations_service.model.dto.UserInfoDto;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Компонент для форматирования сообщений Telegram бота.
 * Отвечает за преобразование данных в красиво отформатированные сообщения
 * с использованием HTML разметки и эмодзи.
 *
 * @see TelegramBotFacade
 * @see TelegramRecommendationHandler
 */
@Component
public class TelegramMessageFormatter {

    /**
     * Форматирует приветственное сообщение согласно требованиям.
     * Требование: "Ответ бота содержит точную фразу: «Здравствуйте {firstName} {lastName}»"
     *
     * @param user данные пользователя для приветствия
     * @return отформатированное приветственное сообщение
     */
    public String formatWelcomeMessage(UserInfoDto user) {
        return "👤 <b>Здравствуйте, %s!</b>".formatted(user.getFullName());
    }

    /**
     * Форматирует сообщение с рекомендациями продуктов.
     * Продукты перечислены в удобно отформатированном списке согласно требованиям.
     *
     * @param recommendations список рекомендаций для форматирования
     * @return отформатированное сообщение с рекомендациями
     */
    public String formatRecommendations(List<RecommendationDTO> recommendations) {
        if (recommendations == null || recommendations.isEmpty()) {
            return "📭 <i>К сожалению, для вас нет подходящих продуктов в данный момент.</i>";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("🎯 <b>Новые продукты для вас:</b>\n\n");

        for (int i = 0; i < recommendations.size(); i++) {
            RecommendationDTO recommendation = recommendations.get(i);
            sb.append(formatSingleRecommendation(i + 1, recommendation));

            if (i < recommendations.size() - 1) {
                sb.append("\n━━━━━━━━━━━━━━━━━━━━\n\n");
            }
        }

        return sb.toString();
    }

    /**
     * Форматирует одну рекомендацию продукта.
     *
     * @param index порядковый номер рекомендации
     * @param recommendation данные рекомендации
     * @return отформатированная строка с рекомендацией
     */
    private String formatSingleRecommendation(int index, RecommendationDTO recommendation) {
        return """
               <b>%d. %s</b>
               %s
               """.formatted(
                index,
                recommendation.name(),
                recommendation.text()
        );
    }

    /**
     * Форматирует полное сообщение с приветствием и рекомендациями.
     * Объединяет приветствие и список продуктов в одном сообщении.
     *
     * @param user данные пользователя
     * @param recommendations список рекомендаций
     * @return полное отформатированное сообщение
     */
    public String formatFullMessage(UserInfoDto user, RecommendationResponse recommendations) {
        String welcome = formatWelcomeMessage(user);
        String recommendationsText = formatRecommendations(recommendations.recommendations());

        return """
               %s
               
               %s
               """.formatted(welcome, recommendationsText);
    }

    /**
     * Форматирует сообщение об отсутствии пользователя.
     *
     * @param username имя, по которому выполнялся поиск
     * @return отформатированное сообщение об ошибке
     */
    public String formatUserNotFoundMessage(String username) {
        return """
               ❌ <b>Пользователь не найден</b>
               
               По запросу <i>"%s"</i> пользователь не найден в системе банка.
               
               💡 <i>Проверьте правильность написания имени и фамилии.</i>
               """.formatted(username);
    }

    /**
     * Форматирует сообщение об ошибке.
     *
     * @param errorMessage текст ошибки
     * @return отформатированное сообщение об ошибке
     */
    public String formatErrorMessage(String errorMessage) {
        return "❌ <b>Ошибка:</b> " + errorMessage;
    }

    /**
     * Форматирует сообщение справки по командам.
     *
     * @return отформатированное сообщение справки
     */
    public String formatHelpMessage() {
        return """
               🏦 <b>Банк "Стар" - Рекомендательная система</b>
               
               <b>Доступные команды:</b>
               /start - показать это сообщение
               /recommend [username] - получить персонализированные рекомендации
               
               <i>Пример: /recommend sheron.berge</i>
               
               💼 <i>Мы подберем для вас лучшие банковские продукты!</i>
               """;
    }

}
