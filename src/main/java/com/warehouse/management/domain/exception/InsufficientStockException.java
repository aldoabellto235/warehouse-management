package com.warehouse.management.domain.exception;

import java.util.UUID;

public class InsufficientStockException extends DomainException {

    public InsufficientStockException(UUID itemId, UUID variantId, int requested, int available) {
        super(String.format(
                "Insufficient stock for item '%s'%s: requested %d, available %d",
                itemId,
                variantId != null ? " / variant '" + variantId + "'" : "",
                requested,
                available
        ));
    }
}
