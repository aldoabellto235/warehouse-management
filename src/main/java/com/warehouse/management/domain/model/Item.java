package com.warehouse.management.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Item {

    private UUID id;
    private String name;
    private String description;
    private BigDecimal basePrice;
    private Instant createdAt;
    private Instant updatedAt;

    private Item() {}

    public static Item create(String name, String description, BigDecimal basePrice) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Item name must not be blank");
        }
        if (basePrice == null || basePrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Base price must be zero or positive");
        }
        Item item = new Item();
        item.id = UUID.randomUUID();
        item.name = name.trim();
        item.description = description;
        item.basePrice = basePrice;
        item.createdAt = Instant.now();
        item.updatedAt = Instant.now();
        return item;
    }

    public static Item reconstitute(UUID id, String name, String description,
                                    BigDecimal basePrice, Instant createdAt, Instant updatedAt) {
        Item item = new Item();
        item.id = Objects.requireNonNull(id);
        item.name = Objects.requireNonNull(name);
        item.description = description;
        item.basePrice = Objects.requireNonNull(basePrice);
        item.createdAt = Objects.requireNonNull(createdAt);
        item.updatedAt = Objects.requireNonNull(updatedAt);
        return item;
    }

    public void update(String name, String description, BigDecimal basePrice) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Item name must not be blank");
        }
        if (basePrice == null || basePrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Base price must be zero or positive");
        }
        this.name = name.trim();
        this.description = description;
        this.basePrice = basePrice;
        this.updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getBasePrice() { return basePrice; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
