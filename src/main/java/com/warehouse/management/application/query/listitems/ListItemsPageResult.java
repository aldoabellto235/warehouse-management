package com.warehouse.management.application.query.listitems;

import java.util.List;

public record ListItemsPageResult(List<ListItemsResult> items, long totalItems) {}
