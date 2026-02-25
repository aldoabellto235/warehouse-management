package com.warehouse.management.application.command.sellitem;

import java.util.UUID;

/**
 * Sells (deducts) a given quantity from stock.
 * Throws InsufficientStockException if stock is too low.
 * Use variantId = null for item-level stock.
 */
public record SellItemCommand(UUID itemId, UUID variantId, int quantity) {}
