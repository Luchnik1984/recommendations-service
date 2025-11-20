package org.skypro.bank.star.recommendations_service.mapper;

import org.skypro.bank.star.recommendations_service.model.dto.UserInfoDto;

import java.util.Map;
import java.util.UUID;

public class UserMapper {

    /**
     * Преобразует данные из базы данных в UserInfoDto.
     * Используется когда данные приходят из SQL запроса (JdbcTemplate).
     *
     * @param id уникальный идентификатор пользователя
     * @param username имя пользователя (логин)
     * @param firstName имя пользователя
     * @param lastName фамилия пользователя
     * @return новый экземпляр UserInfoDto
     */
    public static UserInfoDto toUserInfoDto(UUID id, String username, String firstName, String lastName) {
        return new UserInfoDto(id, username, firstName, lastName);
    }

    /**
     * Преобразует мапу результатов SQL запроса в UserInfoDto.
     * Используется при работе с JdbcTemplate.queryForList.
     *
     * @param rowMap мапа с данными пользователя из БД
     * @return новый экземпляр UserInfoDto
     * @throws IllegalArgumentException если отсутствуют обязательные поля
     */
    public static UserInfoDto mapRow(Map<String, Object> rowMap) {
        UUID id = (UUID) rowMap.get("ID");
        String username = (String) rowMap.get("USERNAME");
        String firstName = (String) rowMap.get("FIRST_NAME");
        String lastName = (String) rowMap.get("LAST_NAME");

        if (id == null || username == null) {
            throw new IllegalArgumentException("User ID and username are required");
        }

        return new UserInfoDto(id, username, firstName, lastName);
    }
}
