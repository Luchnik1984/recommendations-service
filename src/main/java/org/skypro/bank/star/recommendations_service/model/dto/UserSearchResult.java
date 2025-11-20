package org.skypro.bank.star.recommendations_service.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.skypro.bank.star.recommendations_service.enums.SearchStatus;

import java.util.Collections;
import java.util.List;

/**
 * DTO для возврата результата поиска пользователей.
 * Содержит список найденных пользователей и статус поиска для определения
 * дальнейшей логики обработки (выдача рекомендаций, уточнение запроса и т.д.).
 *
 * @see UserInfoDto
 * "@see SearchStatus"
 * "@see org.skypro.bank.star.recommendations_service.service.UserSearchService"
 */

public record UserSearchResult(

        /*
          Список найденных пользователей.
          Может быть пустым в случае NOT_FOUND, содержать одного пользователя
          в случае SINGLE_USER_FOUND или нескольких в случае MULTIPLE_USERS_FOUND.
         */
        @JsonProperty("found_users")
        List<UserInfoDto> foundUsers,

        /*
          Статус результата поиска.
          Определяет семантику содержимого списка foundUsers и дальнейшие действия.
         */
        @JsonProperty("search_status")
        SearchStatus searchStatus
) {

    public UserSearchResult {

        if (foundUsers == null) {
            foundUsers = Collections.emptyList();
        }
        if (searchStatus == null) {
            throw new IllegalArgumentException("SearchStatus cannot be null");
        }

        validateConsistency(foundUsers, searchStatus);
    }

    /**
     * Проверяет согласованность данных между списком пользователей и статусом поиска.
     *
     * @param users список найденных пользователей
     * @param status статус поиска
     * @throws IllegalArgumentException если данные не согласованы
     */
    private static void validateConsistency(List<UserInfoDto> users, SearchStatus status) {
        int userCount = users.size();

        switch (status) {
            case NOT_FOUND:
                if (userCount > 0) {
                    throw new IllegalArgumentException(
                            "NOT_FOUND status requires empty user list, but found " + userCount + " users"
                    );
                }
                break;
            case SINGLE_USER_FOUND:
                if (userCount != 1) {
                    throw new IllegalArgumentException(
                            "SINGLE_USER_FOUND status requires exactly 1 user, but found " + userCount + " users"
                    );
                }
                break;
            case MULTIPLE_USERS_FOUND:
                if (userCount < 2) {
                    throw new IllegalArgumentException(
                            "MULTIPLE_USERS_FOUND status requires at least 2 users, but found " + userCount + " users"
                    );
                }
                break;
        }
    }

    /**
     * Создает результат для случая "пользователь не найден".
     * Статический фабричный метод в стиле существующих утилитарных методов проекта.
     *
     * @return UserSearchResult с пустым списком и статусом NOT_FOUND
     */
    public static UserSearchResult notFound() {
        return new UserSearchResult(Collections.emptyList(), SearchStatus.NOT_FOUND);
    }

    /**
     * Создает результат для случая "найден один пользователь".
     * Статический фабричный метод для удобства создания объектов.
     *
     * @param user найденный пользователь
     * @return UserSearchResult с одним пользователем и статусом SINGLE_USER_FOUND
     * @throws IllegalArgumentException если пользователь равен null
     */
    public static UserSearchResult singleUserFound(UserInfoDto user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null for single user result");
        }
        return new UserSearchResult(List.of(user), SearchStatus.SINGLE_USER_FOUND);
    }

    /**
     * Создает результат для случая "найдено несколько пользователей".
     * Статический фабричный метод для удобства создания объектов.
     *
     * @param users список найденных пользователей (минимум 2)
     * @return UserSearchResult со списком пользователей и статусом MULTIPLE_USERS_FOUND
     * @throws IllegalArgumentException если список содержит менее 2 пользователей
     */
    public static UserSearchResult multipleUsersFound(List<UserInfoDto> users) {
        if (users == null || users.size() < 2) {
            throw new IllegalArgumentException("Multiple users result requires at least 2 users");
        }
        return new UserSearchResult(List.copyOf(users), SearchStatus.MULTIPLE_USERS_FOUND);
    }

}
