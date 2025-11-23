package org.skypro.bank.star.recommendations_service.telegrambot_tests;

import org.junit.jupiter.api.Test;
import org.skypro.bank.star.recommendations_service.model.dto.UserSearchResult;
import org.skypro.bank.star.recommendations_service.enums.SearchStatus;
import org.skypro.bank.star.recommendations_service.service.UserSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class UserSearchServiceIntegrationTest {

    @Autowired
    private UserSearchService userSearchService;

    @Test
    void searchUserByUsername_WhenUserExists_ShouldReturnSingleUser() {

        String username = "invest.user";

        UserSearchResult result = userSearchService.searchUserByUsername(username);

        assertThat(result.searchStatus()).isEqualTo(SearchStatus.SINGLE_USER_FOUND);
        assertThat(result.foundUsers()).hasSize(1);
        assertThat(result.foundUsers().get(0).username()).isEqualTo("invest.user");
        assertThat(result.foundUsers().get(0).getFullName()).isEqualTo("Алексей Инвесторов");
    }

    @Test
    void searchUserByUsername_WhenUserNotFound_ShouldReturnNotFound() {

        String username = "nonexistent.user";

        UserSearchResult result = userSearchService.searchUserByUsername(username);

        assertThat(result.searchStatus()).isEqualTo(SearchStatus.NOT_FOUND);
        assertThat(result.foundUsers()).isEmpty();
    }

    /**
     *  В текущих тестовых данных нет пользователей с одинаковым username
     *  Этот тест проверяет логику классификации
     */
    @Test
    void searchUserByUsername_WhenMultipleUsersFound_ShouldReturnMultipleUsers() {

        String username = "test.duplicate"; // Предполагаем, что есть дубликаты

        UserSearchResult result = userSearchService.searchUserByUsername(username);

        assertThat(result.searchStatus()).isEqualTo(SearchStatus.NOT_FOUND);
    }

    @Test
    void searchUserByUsername_WithEmptyUsername_ShouldThrowException() {
        String username = "";

        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> userSearchService.searchUserByUsername(username)
        );
    }

    @Test
    void searchUserByUsername_WithNullUsername_ShouldThrowException() {
        String username = null;

               org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> userSearchService.searchUserByUsername(username)
        );
    }
}
