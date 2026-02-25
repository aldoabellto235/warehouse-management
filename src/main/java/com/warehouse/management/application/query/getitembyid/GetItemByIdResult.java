package com.warehouse.management.application.query.getitembyid;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record GetItemByIdResult(
        UUID id,
        String name,
        String description,
        BigDecimal basePrice,
        Instant createdAt,
        Instant updatedAt
) {}
