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
     * Ищет активных пользователей поточному username.
     *
     * @param username точное имя пользователя для поиска
     * @return список найденных пользователей (обычно 0 или 1 элемент)
     */
    List<UserInfoDto> findActiveUsersByUsername(String username);
}




