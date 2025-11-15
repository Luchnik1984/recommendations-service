package org.skypro.bank.star.recommendations_service.cache;

import org.skypro.bank.star.recommendations_service.enums.ProductType;

import java.util.UUID;

public record ProductTypeKey (UUID userId, ProductType productType){

    public UUID getUserId() {
        return userId;
    }

    public ProductType getProductType() {
        return productType;
    }
}
