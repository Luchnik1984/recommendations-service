package org.skypro.bank.star.recommendations_service.service;

import org.skypro.bank.star.recommendations_service.model.dto.UserInfoDto;
import org.skypro.bank.star.recommendations_service.model.dto.UserSearchResult;
import org.skypro.bank.star.recommendations_service.repository.UserDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для поиска пользователей банка по имени и фамилии.
 * Обеспечивает бизнес-логику поиска и классификацию результатов
 * согласно требованиям Telegram бота.
 *
 * @see org.skypro.bank.star.recommendations_service.repository.UserDataRepository
 * @see org.skypro.bank.star.recommendations_service.model.dto.UserSearchResult
 */

@Service
public class UserSearchService {
    private static final Logger logger = LoggerFactory.getLogger(UserSearchService.class);

    private final UserDataRepository userDataRepository;

    /**
     * Конструктор сервиса поиска пользователей.
     *
     * @param userDataRepository репозиторий для доступа к данным пользователей
     */
    public UserSearchService(UserDataRepository userDataRepository) {
        this.userDataRepository = userDataRepository;
    }

    /**
     * Основной метод поиска пользователей по имени или фамилии.
     * Ищет активных пользователей, у которых имя или фамилия содержат указанную строку.
     * Возвращает результат с классификацией согласно бизнес-требованиям.
     *
     * @param searchString строка для поиска (имя или фамилия)
     * @return UserSearchResult с найденными пользователями и статусом поиска
     * @throws IllegalArgumentException если searchString null или пустой
     */
    public UserSearchResult searchUser(String searchString) {
        logger.debug("Searching users by name: '{}'", searchString);

        if (searchString == null || searchString.trim().isEmpty()) {
            throw new IllegalArgumentException("Search string cannot be null or empty");
        }

        String trimmedSearchString = searchString.trim();
        List<UserInfoDto> foundUsers = userDataRepository.findActiveUsersByName(trimmedSearchString);

        logger.debug("Found {} users for search: '{}'", foundUsers.size(), trimmedSearchString);

        // Классификация результатов согласно бизнес-требованиям
        return classifySearchResult(foundUsers);
    }

    /**
     * Классифицирует результаты поиска согласно бизнес-требованиям.
     * Определяет статус поиска на основе количества найденных пользователей.
     *
     * @param foundUsers список найденных пользователей
     * @return UserSearchResult с соответствующим статусом поиска
     */
    private UserSearchResult classifySearchResult(List<UserInfoDto> foundUsers) {
        int userCount = foundUsers.size();

        if (userCount == 0) {
            return UserSearchResult.notFound();
        } else if (userCount == 1) {
            return UserSearchResult.singleUserFound(foundUsers.get(0));
        } else {
            return UserSearchResult.multipleUsersFound(foundUsers);
        }
    }
}
