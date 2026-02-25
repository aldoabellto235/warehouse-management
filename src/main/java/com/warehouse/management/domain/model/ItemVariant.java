package com.warehouse.management.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class ItemVariant {

    private UUID id;
    private UUID itemId;
    private String name;
    private String sku;
    private BigDecimal price;
    private Map<String, String> attributes;
    private Instant createdAt;
    private Instant updatedAt;

    private ItemVariant() {}

    public static ItemVariant create(UUID itemId, String name, String sku,
                                     BigDecimal price, Map<String, String> attributes) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Variant name must not be blank");
        }
        if (sku == null || sku.isBlank()) {
            throw new IllegalArgumentException("SKU must not be blank");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price must be zero or positive");
        }
        ItemVariant variant = new ItemVariant();
        variant.id = UUID.randomUUID();
        variant.itemId = Objects.requireNonNull(itemId);
        variant.name = name.trim();
        variant.sku = sku.trim().toUpperCase();
        variant.price = price;
        variant.attributes = attributes != null ? Map.copyOf(attributes) : Collections.emptyMap();
        variant.createdAt = Instant.now();
        variant.updatedAt = Instant.now();
        return variant;
    }

    public static ItemVariant reconstitute(UUID id, UUID itemId, String name, String sku,
                                           BigDecimal price, Map<String, String> attributes,
                                           Instant createdAt, Instant updatedAt) {
        ItemVariant variant = new ItemVariant();
        variant.id = Objects.requireNonNull(id);
        variant.itemId = Objects.requireNonNull(itemId);
        variant.name = Objects.requireNonNull(name);
        variant.sku = Objects.requireNonNull(sku);
        variant.price = Objects.requireNonNull(price);
        variant.attributes = attributes != null ? Map.copyOf(attributes) : Collections.emptyMap();
        variant.createdAt = Objects.requireNonNull(createdAt);
        variant.updatedAt = Objects.requireNonNull(updatedAt);
        return variant;
    }

    public void update(String name, String sku, BigDecimal price, Map<String, String> attributes) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Variant name must not be blank");
        }
        if (sku == null || sku.isBlank()) {
            throw new IllegalArgumentException("SKU must not be blank");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price must be zero or positive");
        }
        this.name = name.trim();
        this.sku = sku.trim().toUpperCase();
        this.price = price;
        this.attributes = attributes != null ? Map.copyOf(attributes) : Collections.emptyMap();
        this.updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getItemId() { return itemId; }
    public String getName() { return name; }
    public String getSku() { return sku; }
    public BigDecimal getPrice() { return price; }
    public Map<String, String> getAttributes() { return attributes; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
