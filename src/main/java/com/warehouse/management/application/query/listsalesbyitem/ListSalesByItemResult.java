package com.warehouse.management.application.query.listsalesbyitem;

import java.time.Instant;
import java.util.UUID;

public record ListSalesByItemResult(
        UUID saleId,
        UUID itemId,
        UUID variantId,
        int quantity,
        Instant soldAt
) {}
