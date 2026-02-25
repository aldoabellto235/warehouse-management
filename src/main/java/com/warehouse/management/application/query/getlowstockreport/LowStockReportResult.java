package com.warehouse.management.application.query.getlowstockreport;

import java.util.UUID;

public record LowStockReportResult(
        UUID itemId,
        String itemName,
        UUID variantId,
        String variantName,
        String sku,
        int quantity,
        int threshold
) {}
