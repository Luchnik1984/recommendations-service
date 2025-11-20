package org.skypro.bank.star.recommendations_service.service;

import org.skypro.bank.star.recommendations_service.model.dto.UserInfoDto;
import org.skypro.bank.star.recommendations_service.model.dto.UserSearchResult;
import org.skypro.bank.star.recommendations_service.repository.UserDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для поиска пользователей банка по имени и фамилии.
 * Обеспечивает бизнес-логику поиска и классификацию результатов
 * согласно требованиям Telegram бота.
 *
 * @see UserDataRepository
 * @see UserSearchResult
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
    public UserSearchService(@Qualifier("cachedUserDataRepository") UserDataRepository userDataRepository) {
        this.userDataRepository = userDataRepository;
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

    /**
     * Ищет пользователя по username.
     * Согласно требованиям команды /recommend username.
     *
     * @param username точное имя пользователя для поиска
     * @return UserSearchResult с найденным пользователем или NOT_FOUND
     */
    public UserSearchResult searchUserByUsername(String username) {
        logger.debug("Searching users by username: '{}'", username);

        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        String trimmedUsername = username.trim();
        List<UserInfoDto> foundUsers = userDataRepository.findActiveUsersByUsername(trimmedUsername);

        logger.debug("Found {} users for username: '{}'", foundUsers.size(), trimmedUsername);

        return classifySearchResult(foundUsers);
    }
}
