package com.warehouse.management.application.command.adjuststock;

import java.util.UUID;

/**
 * Adjusts stock for an item or a specific variant.
 * Use variantId = null for item-level stock (items with no variants).
 * delta > 0 = restock (add), delta < 0 = deduct.
 */
public record AdjustStockCommand(UUID itemId, UUID variantId, int delta) {}
