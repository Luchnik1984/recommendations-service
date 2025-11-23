package org.skypro.bank.star.recommendations_service.repository;

import com.github.benmanes.caffeine.cache.Cache;
import org.skypro.bank.star.recommendations_service.cache.ProductTypeKey;
import org.skypro.bank.star.recommendations_service.cache.TransactionTypeKey;
import org.skypro.bank.star.recommendations_service.enums.ProductType;
import org.skypro.bank.star.recommendations_service.enums.TransactionType;
import org.skypro.bank.star.recommendations_service.model.dto.UserInfoDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
@Qualifier("CachedUserDataRepository")
public class CachedUserDataRepository implements UserDataRepository {

    public final UserDataRepository userDataRepository;
    public final Cache<ProductTypeKey, Boolean> hasProductTypeCache;
    private final Cache<TransactionTypeKey, BigDecimal> totalAmountCache;
    private final Cache<ProductTypeKey, Integer> transactionCountCache;

    public CachedUserDataRepository(@Qualifier("UserDataRepositoryImpl") UserDataRepository userDataRepository
            , Cache<ProductTypeKey, Boolean> hasProductTypeCache
            , Cache<TransactionTypeKey, BigDecimal> totalAmountCache
            , Cache<ProductTypeKey, Integer> transactionCountCache) {
        this.userDataRepository = userDataRepository;
        this.hasProductTypeCache = hasProductTypeCache;
        this.totalAmountCache = totalAmountCache;
        this.transactionCountCache = transactionCountCache;
    }

    @Override
    public boolean hasProductType(UUID userId, ProductType productType) {
        ProductTypeKey key = new ProductTypeKey(userId, productType);
        return Boolean.TRUE.equals(hasProductTypeCache.get(key, k ->
                userDataRepository.hasProductType(k.userId(), k.productType())));
    }

    @Override
    public BigDecimal getTotalAmount(UUID userId, ProductType productType, TransactionType transactionType) {
        TransactionTypeKey key = new TransactionTypeKey(userId, productType, transactionType);
        return totalAmountCache.get(key, k -> {
            return userDataRepository.getTotalAmount(k.userId(), k.productType(), k.transactionType());
        });
    }

    @Override
    public boolean isDepositsGreaterThanWithdrawals(UUID userId, ProductType productType) {
        BigDecimal deposits = getTotalAmount(userId, productType, TransactionType.DEPOSIT);
        BigDecimal spends = getTotalAmount(userId, productType, TransactionType.WITHDRAW);
        return deposits.compareTo(spends) > 0;
    }

    @Override
    public int getTransactionCountByProductType(UUID userId, ProductType productType) {
        ProductTypeKey key = new ProductTypeKey(userId, productType);
        Integer result = transactionCountCache.get(key, k ->
                userDataRepository.getTransactionCountByProductType(k.userId(), k.productType()));
        return result != null ? result : 0;
    }

    @Override
    public double getTransactionSumByType(UUID userId, ProductType productType, TransactionType transactionType) {
        BigDecimal totalAmount = getTotalAmount(userId, productType, transactionType);
        return totalAmount.doubleValue();
    }


    /**
     * Метод для очистки кэша
     */
    public void clearAllCaches() {
        hasProductTypeCache.invalidateAll();
        totalAmountCache.invalidateAll();
        transactionCountCache.invalidateAll();
    }

    @Override
    public List<UserInfoDto> findActiveUsersByUsername(String username) {
        return userDataRepository.findActiveUsersByUsername(username);
    }

}
