package com.warehouse.management.domain.exception;

public class DuplicateSkuException extends DomainException {

    public DuplicateSkuException(String sku) {
        super("A variant with SKU '" + sku + "' already exists");
    }
}
