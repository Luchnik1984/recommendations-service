package org.skypro.bank.star.recommendations_service.enums;

/**
 * Перечисление статусов результата поиска пользователей.
 * Определяет все возможные исходы операции поиска пользователя по имени/фамилии.
 * Используется в {"@link UserSearchResult"} для классификации результатов поиска.
 * @see org.skypro.bank.star.recommendations_service.service.UserSearchService
 * @see org.skypro.bank.star.recommendations_service.model.dto.UserSearchResult
 */
public enum SearchStatus {

    /**
     * Пользователь не найден.
     * Соответствует ситуации, когда в базе данных нет пользователей,
     * удовлетворяющих критериям поиска.
     */
    NOT_FOUND,

    /**
     * Найден ровно один пользователь.
     * Идеальный случай для выдачи персонализированных рекомендаций.
     */
    SINGLE_USER_FOUND,

    /**
     * Найдено несколько пользователей.
     * Требует уточнения поискового запроса или выбора конкретного пользователя.
     * Согласно требованиям, в этом случае бот возвращает "Пользователь не найден".
     */
    MULTIPLE_USERS_FOUND
}

