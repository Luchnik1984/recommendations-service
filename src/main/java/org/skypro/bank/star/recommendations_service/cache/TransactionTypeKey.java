package org.skypro.bank.star.recommendations_service.cache;

import org.skypro.bank.star.recommendations_service.enums.ProductType;
import org.skypro.bank.star.recommendations_service.enums.TransactionType;

import java.util.UUID;

public record TransactionTypeKey (UUID userId, ProductType productType, TransactionType transactionType){
   }
