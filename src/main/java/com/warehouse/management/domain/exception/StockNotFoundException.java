package com.warehouse.management.domain.exception;

import java.util.UUID;

public class StockNotFoundException extends DomainException {

    public StockNotFoundException(UUID itemId, UUID variantId) {
        super(String.format(
                "Stock record not found for item '%s'%s",
                itemId,
                variantId != null ? " / variant '" + variantId + "'" : ""
        ));
    }
}
