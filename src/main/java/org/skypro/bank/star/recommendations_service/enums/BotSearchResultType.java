package org.skypro.bank.star.recommendations_service.enums;

/**
 * Перечисление типов результата поиска для Telegram бота.
 * Определяет возможные исходы операции поиска пользователя и получения рекомендаций.
 *
 * @see org.skypro.bank.star.recommendations_service.service.TelegramBotFacade
 */
public enum BotSearchResultType {

    /**
     * Успешный поиск и получение рекомендаций.
     * Пользователь найден и для него есть рекомендации.
     */
    SUCCESS,

    /**
     * Пользователь не найден.
     * Включает случаи NOT_FOUND и MULTIPLE_USERS_FOUND согласно требованиям.
     */
    USER_NOT_FOUND,

    /**
     * Произошла ошибка при выполнении операции.
     * Требует повторной попытки или административного вмешательства.
     */
    ERROR
}
