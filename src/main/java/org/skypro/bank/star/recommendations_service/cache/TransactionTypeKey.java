package org.skypro.bank.star.recommendations_service.cache;

import org.skypro.bank.star.recommendations_service.enums.ProductType;
import org.skypro.bank.star.recommendations_service.enums.TransactionType;

import java.util.UUID;

public record TransactionTypeKey (UUID userId, ProductType productType, TransactionType transactionType){
    @Override
    public UUID userId() {
        return userId;
    }

    @Override
    public ProductType productType() {
        return productType;
    }

    @Override
    public TransactionType transactionType() {
        return transactionType;
    }
}
