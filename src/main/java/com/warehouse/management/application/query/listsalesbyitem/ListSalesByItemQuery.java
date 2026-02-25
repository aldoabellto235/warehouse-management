package com.warehouse.management.application.query.listsalesbyitem;

import java.util.UUID;

public record ListSalesByItemQuery(UUID itemId, UUID variantId, int page, int size) {}
