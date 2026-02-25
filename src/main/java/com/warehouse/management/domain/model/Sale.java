package com.warehouse.management.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Sale {

    private UUID id;
    private UUID itemId;
    private UUID variantId;
    private int quantity;
    private Instant soldAt;

    private Sale() {}

    public static Sale record(UUID itemId, UUID variantId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Sale quantity must be positive");
        }
        Sale sale = new Sale();
        sale.id = UUID.randomUUID();
        sale.itemId = Objects.requireNonNull(itemId);
        sale.variantId = variantId;
        sale.quantity = quantity;
        sale.soldAt = Instant.now();
        return sale;
    }

    public static Sale reconstitute(UUID id, UUID itemId, UUID variantId, int quantity, Instant soldAt) {
        Sale sale = new Sale();
        sale.id = Objects.requireNonNull(id);
        sale.itemId = Objects.requireNonNull(itemId);
        sale.variantId = variantId;
        sale.quantity = quantity;
        sale.soldAt = Objects.requireNonNull(soldAt);
        return sale;
    }

    public UUID getId() { return id; }
    public UUID getItemId() { return itemId; }
    public UUID getVariantId() { return variantId; }
    public int getQuantity() { return quantity; }
    public Instant getSoldAt() { return soldAt; }
}
