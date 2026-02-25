package com.warehouse.management.application.query.getlowstockreport;

import java.util.List;

public record LowStockReportPageResult(List<LowStockReportResult> entries, long total) {}
