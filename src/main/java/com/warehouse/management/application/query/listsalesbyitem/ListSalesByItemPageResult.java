package com.warehouse.management.application.query.listsalesbyitem;

import java.util.List;

public record ListSalesByItemPageResult(List<ListSalesByItemResult> sales, long totalSales) {}
