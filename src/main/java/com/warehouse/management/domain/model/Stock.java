package com.warehouse.management.domain.model;

import com.warehouse.management.domain.exception.InsufficientStockException;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Stock {

    private UUID id;
    private UUID itemId;
    private UUID variantId;  // null = item-level stock (no variants)
    private int quantity;
    private Instant createdAt;
    private Instant updatedAt;

    private Stock() {}

    public static Stock create(UUID itemId, UUID variantId) {
        Stock stock = new Stock();
        stock.id = UUID.randomUUID();
        stock.itemId = Objects.requireNonNull(itemId);
        stock.variantId = variantId;
        stock.quantity = 0;
        stock.createdAt = Instant.now();
        stock.updatedAt = Instant.now();
        return stock;
    }

    public static Stock reconstitute(UUID id, UUID itemId, UUID variantId,
                                     int quantity, Instant createdAt, Instant updatedAt) {
        Stock stock = new Stock();
        stock.id = Objects.requireNonNull(id);
        stock.itemId = Objects.requireNonNull(itemId);
        stock.variantId = variantId;
        stock.quantity = quantity;
        stock.createdAt = Objects.requireNonNull(createdAt);
        stock.updatedAt = Objects.requireNonNull(updatedAt);
        return stock;
    }

    public void add(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount to add must be positive");
        }
        this.quantity += amount;
        this.updatedAt = Instant.now();
    }

    public void deduct(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount to deduct must be positive");
        }
        if (this.quantity < amount) {
            throw new InsufficientStockException(itemId, variantId, amount, this.quantity);
        }
        this.quantity -= amount;
        this.updatedAt = Instant.now();
    }

    public boolean isAvailable(int requested) {
        return this.quantity >= requested;
    }

    public UUID getId() { return id; }
    public UUID getItemId() { return itemId; }
    public UUID getVariantId() { return variantId; }
    public int getQuantity() { return quantity; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
