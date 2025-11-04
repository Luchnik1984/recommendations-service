package org.skypro.bank.star.recommendations_service.repository;

import org.skypro.bank.star.recommendations_service.enums.ProductType;
import org.skypro.bank.star.recommendations_service.enums.TransactionType;

import java.math.BigDecimal;
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

}

