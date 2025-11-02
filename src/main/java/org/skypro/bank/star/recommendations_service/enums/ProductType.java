package org.skypro.bank.star.recommendations_service.enums;

/**
 * Перечисление типов продуктов банка.
 * Обеспечивает типобезопасность при работе с типами продуктов
 */

public enum ProductType {
    DEBIT("DEBIT"),
    CREDIT("CREDIT"),
    SAVING("SAVING"),
    INVEST("INVEST");

    private final String value;

    ProductType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

