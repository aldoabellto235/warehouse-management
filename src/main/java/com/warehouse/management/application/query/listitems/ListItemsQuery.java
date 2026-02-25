package com.warehouse.management.application.query.listitems;

public record ListItemsQuery(
        String name,        // optional — filters by name (case-insensitive contains)
        Boolean inStockOnly,// optional — when true, only items with quantity > 0
        int page,
        int size
) {}
