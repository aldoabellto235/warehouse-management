package com.warehouse.management.application.query.liststocks;

import java.util.UUID;

public record ListStocksResult(
        UUID stockId,
        UUID itemId,
        UUID variantId,
        int quantity,
        boolean available
) {}
