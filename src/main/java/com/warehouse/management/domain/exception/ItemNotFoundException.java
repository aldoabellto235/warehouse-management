package com.warehouse.management.domain.exception;

import java.util.UUID;

public class ItemNotFoundException extends DomainException {

    public ItemNotFoundException(UUID itemId) {
        super("Item with id '" + itemId + "' not found");
    }
}
