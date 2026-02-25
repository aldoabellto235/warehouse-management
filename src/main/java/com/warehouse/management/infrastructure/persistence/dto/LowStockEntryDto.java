package com.warehouse.management.infrastructure.persistence.dto;

import java.util.UUID;

public record LowStockEntryDto(
        UUID itemId,
        String itemName,
        UUID variantId,
        String variantName,
        String sku,
        int quantity
) {}
