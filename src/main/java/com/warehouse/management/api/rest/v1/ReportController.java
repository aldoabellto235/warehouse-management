package com.warehouse.management.api.rest.v1;

import com.warehouse.management.api.rest.v1.response.LowStockReportResponse;
import com.warehouse.management.api.rest.v1.response.PagedResponse;
import com.warehouse.management.application.query.getlowstockreport.GetLowStockReportQuery;
import com.warehouse.management.application.query.getlowstockreport.GetLowStockReportQueryHandler;
import com.warehouse.management.application.query.getlowstockreport.LowStockReportPageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Reports", description = "Inventory reports and alerts")
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final GetLowStockReportQueryHandler lowStockHandler;

    @Operation(
            summary = "Low stock report",
            description = "Returns all items and variants whose stock quantity is at or below the given threshold. " +
                    "Sorted by quantity ascending so the most critical items appear first.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Report retrieved",
                    content = @Content(schema = @Schema(implementation = PagedResponse.class)))
    })
    @GetMapping("/low-stock")
    public PagedResponse<LowStockReportResponse> getLowStock(
            @Parameter(description = "Stock alert threshold (inclusive)", example = "5")
            @RequestParam(defaultValue = "5") @Min(0) @Max(10000) int threshold,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Page size (max 100)") @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {

        LowStockReportPageResult result = lowStockHandler.handle(
                new GetLowStockReportQuery(threshold, page, size));

        List<LowStockReportResponse> entries = result.entries().stream()
                .map(e -> new LowStockReportResponse(
                        e.itemId(), e.itemName(), e.variantId(),
                        e.variantName(), e.sku(), e.quantity(), e.threshold()))
                .toList();

        return PagedResponse.success(entries, page, size, result.total());
    }
}
