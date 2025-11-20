package org.skypro.bank.star.recommendations_service.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * DTO для передачи информации о пользователе банка.
 * Содержит основные идентификационные данные пользователя,
 * необходимые для поиска и отображения в интерфейсах (API, Telegram бот).
 * @see org.skypro.bank.star.recommendations_service.mapper.UserMapper
 * Данный соответствуют полям базы данных transactions в таблице USERS:
 *
 * @param id        - соответствует полю ID
 * @param username  - соответствует полю USERNAME
 * @param firstName - соответствует полю FIRST_NAME
 * @param lastName  - соответствует полю LAST_NAME
 */
public record UserInfoDto(

        @JsonProperty("id")
        @NotNull(message = "User ID cannot be null")
        UUID id,

        @JsonProperty("username")
        @NotNull(message = "Username cannot be null")
        @Size(min = 1, message = "Username cannot be empty")
        String username,

        @JsonProperty("first_name")
        String firstName,

        @JsonProperty("last_name")
        String lastName
) {

    public UserInfoDto {
        if (username != null && username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be blank");
        }

        firstName = firstName != null ? firstName : "";
        lastName = lastName != null ? lastName : "";
    }

    /**
     * Возвращает полное имя пользователя в формате "Имя Фамилия".
     * Используется для приветствия в Telegram боте согласно требованиям.
     * Если какая-то часть имени отсутствует, используется пустая строка.
     *
     * @return полное имя пользователя в формате "Имя Фамилия"
     */
    public String getFullName() {
        return (firstName + " " + lastName).trim();
    }

}
