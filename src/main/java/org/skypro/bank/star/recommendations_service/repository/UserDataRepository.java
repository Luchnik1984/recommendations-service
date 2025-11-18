package org.skypro.bank.star.recommendations_service.repository;

import org.skypro.bank.star.recommendations_service.enums.ProductType;
import org.skypro.bank.star.recommendations_service.enums.TransactionType;
import org.skypro.bank.star.recommendations_service.model.dto.UserInfoDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Интерфейс репозитория для работы с финансовыми данными пользователей.
 * Определяет контракт для доступа к данным без привязки к реализации
 */

public interface UserDataRepository {

    /**
     * Проверяет, есть ли у пользователя транзакции по продуктам указанного типа
     */
    boolean hasProductType(UUID userId, ProductType productType);

    /**
     * Получает общую сумму по типу транзакции и типу продукта
     */
    BigDecimal getTotalAmount(UUID userId, ProductType productType, TransactionType transactionType);

    /**
     * Проверяем, больше ли сумма пополнений суммы трат для указанного типа продукта
     */
    boolean isDepositsGreaterThanWithdrawals(UUID userId, ProductType productType);

    /**
     * Получает количество всех транзакций пользователя по продуктам указанного типа
     */
    int getTransactionCountByProductType(UUID userId, ProductType productType);

    /**
     * Получает сумму транзакций пользователя по типу продукта и типу транзакции
     */
    double getTransactionSumByType(UUID userId, ProductType productType, TransactionType transactionType);

    /**
     * Ищет пользователей по имени или фамилии (case-insensitive поиск).
     * Использует частичное совпадение (LIKE) для поиска по подстроке.
     * Возвращает список пользователей, у которых имя или фамилия содержат указанную строку.
     *
     * @param searchString строка для поиска (имя или фамилия)
     * @return список найденных пользователей, отсортированный по имени и фамилии
     * @throws IllegalArgumentException если searchString null или пустой
     */
    List<UserInfoDto> findUsersByName(String searchString);

    /**
     * Ищет активных пользователей по имени или фамилии (case-insensitive поиск).
     * Возвращает только пользователей, имеющих транзакции (активных).
     * Использует частичное совпадение (LIKE) для поиска по подстроке.
     *
     * @param searchString строка для поиска (имя или фамилия)
     * @return список найденных АКТИВНЫХ пользователей, отсортированный по имени и фамилии
     * @throws IllegalArgumentException если searchString null или пустой
     */
    List<UserInfoDto> findActiveUsersByName(String searchString);

    /**
     * Ищет активных пользователей по точному username.
     *
     * @param username точное имя пользователя для поиска
     * @return список найденных пользователей (обычно 0 или 1 элемент)
     */
    List<UserInfoDto> findActiveUsersByUsername(String username);
}




