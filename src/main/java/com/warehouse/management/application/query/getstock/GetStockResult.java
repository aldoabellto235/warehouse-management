package com.warehouse.management.application.query.getstock;

import java.util.UUID;

public record GetStockResult(
        UUID stockId,
        UUID itemId,
        UUID variantId,
        int quantity,
        boolean available
) {}
