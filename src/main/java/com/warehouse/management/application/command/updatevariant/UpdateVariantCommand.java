package com.warehouse.management.application.command.updatevariant;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public record UpdateVariantCommand(
        UUID variantId,
        String name,
        String sku,
        BigDecimal price,
        Map<String, String> attributes
) {}
