package org.skypro.bank.star.recommendations_service.telegram;

import org.skypro.bank.star.recommendations_service.enums.BotSearchResultType;
import org.skypro.bank.star.recommendations_service.model.dto.RecommendationResponse;
import org.skypro.bank.star.recommendations_service.model.dto.UserInfoDto;
import org.skypro.bank.star.recommendations_service.model.dto.UserSearchResult;
import org.skypro.bank.star.recommendations_service.service.RecommendationService;
import org.skypro.bank.star.recommendations_service.service.UserSearchService;
import org.skypro.bank.star.recommendations_service.telegram.formater.TelegramMessageFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Фасад для работы с Telegram ботом, объединяющий функциональность
 * поиска пользователей и получения рекомендаций.
 * Обеспечивает единую точку доступа для бизнес-логики бота.
 *
 * @see UserSearchService
 * @see RecommendationService
 * @see TelegramRecommendationHandler
 */
@Service
public class TelegramBotFacade {

    private static final Logger logger = LoggerFactory.getLogger(TelegramBotFacade.class);

    private final UserSearchService userSearchService;
    private final RecommendationService recommendationService;
    private final TelegramMessageFormatter messageFormatter;

    /**
     * Конструктор фасада для Telegram бота.
     *
     * @param userSearchService сервис поиска пользователей
     * @param recommendationService сервис рекомендаций
     */
    public TelegramBotFacade(UserSearchService userSearchService,
                             RecommendationService recommendationService,
                             TelegramMessageFormatter messageFormatter) {
        this.userSearchService = userSearchService;
        this.recommendationService = recommendationService;
        this.messageFormatter = messageFormatter;

        logger.info("TelegramBotFacade initialized");
    }

    /**
     * Выполняет поиск пользователя по username и получение рекомендаций.
     * Согласно требованиям команды /recommend username.
     *
     * @param username имя пользователя для поиска
     * @return результат поиска с рекомендациями
     */
    public BotSearchResult findUserAndRecommendationsByUsername(String username) {
        logger.debug("Starting search and recommendations for username: '{}'", username);

        long startTime = System.currentTimeMillis();
        UserSearchResult searchResult = userSearchService.searchUserByUsername(username);

        switch (searchResult.searchStatus()) {
            case NOT_FOUND:
                logger.info("User not found for search: '{}'",username);
                return BotSearchResult.userNotFound(username);

            case MULTIPLE_USERS_FOUND:
                logger.info("Multiple users found for search: '{}'", username);
                return BotSearchResult.userNotFound(username);

            case SINGLE_USER_FOUND:
                UserInfoDto user = searchResult.foundUsers().get(0);
                logger.info("Single user found: {} (ID: {})", user.getFullName(), user.id());
                return getUserRecommendations(user);

            default:
                logger.error("Unknown search status: {}", searchResult.searchStatus());
                return BotSearchResult.error("Неизвестный статус поиска");
        }
    }



    /**
     * Получает рекомендации для найденного пользователя.
     *
     * @param user найденный пользователь
     * @return результат с рекомендациями для пользователя
     */
    private BotSearchResult getUserRecommendations(UserInfoDto user) {
        try {
            long startTime = System.currentTimeMillis();
            RecommendationResponse recommendations = recommendationService.getRecommendationsForUser(user.id());
            long duration = System.currentTimeMillis() - startTime;

            logger.debug("Recommendations retrieved in {}ms for user: {}. Count: {}",
                    duration, user.getFullName(), recommendations.recommendations().size());

            if (recommendations.recommendations().isEmpty()) {
                logger.info("No recommendations found for user: {}", user.getFullName());
            } else {
                logger.info("Found {} recommendations for user: {}",
                        recommendations.recommendations().size(), user.getFullName());
            }

            return BotSearchResult.success(user, recommendations);

        } catch (Exception e) {
            logger.error("Error getting recommendations for user {} ({}): {}",
                    user.id(), user.getFullName(), e.getMessage(), e);
            return BotSearchResult.error("Ошибка при получении рекомендаций");
        }
    }

    /**
     * Внутренний класс для представления результата работы фасада.
     * Содержит все необходимые данные для формирования ответа бота.
     */
    public static class BotSearchResult {
        private final boolean success;
        private final String message;
        private final UserInfoDto user;
        private final RecommendationResponse recommendations;
        private final BotSearchResultType resultType;

        private BotSearchResult(boolean success, String message, UserInfoDto user,
                                RecommendationResponse recommendations, BotSearchResultType resultType) {
            this.success = success;
            this.message = message;
            this.user = user;
            this.recommendations = recommendations;
            this.resultType = resultType;
        }


        public static BotSearchResult userNotFound(String searchName) {
            return new BotSearchResult(false, "Пользователь не найден", null, null, BotSearchResultType.USER_NOT_FOUND);
        }

        public static BotSearchResult success(UserInfoDto user, RecommendationResponse recommendations) {
            return new BotSearchResult(true, "Рекомендации найдены", user, recommendations, BotSearchResultType.SUCCESS);
        }

        public static BotSearchResult error(String errorMessage) {
            return new BotSearchResult(false, errorMessage, null, null, BotSearchResultType.ERROR);
        }


        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public UserInfoDto getUser() { return user; }
        public RecommendationResponse getRecommendations() { return recommendations; }
        public BotSearchResultType getResultType() { return resultType; }
    }

    /**
     * Получает отформатированное сообщение для найденного пользователя с рекомендациями.
     *
     * @param user найденный пользователь
     * @param recommendations рекомендации для пользователя
     * @return отформатированное сообщение для отправки в Telegram
     */
    public String getFormattedMessage(UserInfoDto user, RecommendationResponse recommendations) {
        logger.debug("Formatting message for user: {} with {} recommendations",
                user.getFullName(), recommendations.recommendations().size());
        return messageFormatter.formatFullMessage(user, recommendations);
    }

    /**
     * Получает отформатированное сообщение об отсутствии пользователя.
     *
     * @param searchName имя, по которому выполнялся поиск
     * @return отформатированное сообщение об ошибке
     */
    public String getFormattedUserNotFoundMessage(String searchName) {
        logger.debug("Formatting user not found message for search: '{}'", searchName);
        return messageFormatter.formatUserNotFoundMessage(searchName);
    }

}
