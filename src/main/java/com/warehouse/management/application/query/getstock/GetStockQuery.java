package com.warehouse.management.application.query.getstock;

import java.util.UUID;

public record GetStockQuery(UUID itemId, UUID variantId) {}
