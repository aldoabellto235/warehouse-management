package com.warehouse.management.application.query.listitems;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ListItemsResult(
        UUID id,
        String name,
        String description,
        BigDecimal basePrice,
        Instant createdAt,
        Instant updatedAt
) {}
