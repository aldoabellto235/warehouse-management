package com.warehouse.management.application.query.listvariantsbyitem;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record ListVariantsByItemResult(
        UUID id,
        UUID itemId,
        String name,
        String sku,
        BigDecimal price,
        Map<String, String> attributes,
        Instant createdAt,
        Instant updatedAt
) {}
