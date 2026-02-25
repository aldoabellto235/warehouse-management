package com.warehouse.management.application.query.getitemsummary;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record GetItemSummaryResult(
        UUID id,
        String name,
        String description,
        BigDecimal basePrice,
        boolean hasVariants,
        int totalStock,
        List<VariantStock> variants,
        Instant createdAt,
        Instant updatedAt
) {
    public record VariantStock(
            UUID variantId,
            String name,
            String sku,
            BigDecimal price,
            Map<String, String> attributes,
            int stock,
            boolean available
    ) {}
}
