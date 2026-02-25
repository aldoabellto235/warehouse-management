package com.warehouse.management.domain.exception;

import java.util.UUID;

public class VariantNotFoundException extends DomainException {

    public VariantNotFoundException(UUID variantId) {
        super("Variant with id '" + variantId + "' not found");
    }
}
