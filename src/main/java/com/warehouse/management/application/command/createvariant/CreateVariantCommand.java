package com.warehouse.management.application.command.createvariant;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public record CreateVariantCommand(
        UUID itemId,
        String name,
        String sku,
        BigDecimal price,
        Map<String, String> attributes
) {}
