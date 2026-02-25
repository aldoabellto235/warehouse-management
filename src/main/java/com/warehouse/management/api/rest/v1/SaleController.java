package com.warehouse.management.api.rest.v1;

import com.warehouse.management.api.exception.ErrorResponse;
import com.warehouse.management.api.rest.v1.response.PagedResponse;
import com.warehouse.management.api.rest.v1.response.SaleResponse;
import com.warehouse.management.application.query.listsalesbyitem.ListSalesByItemPageResult;
import com.warehouse.management.application.query.listsalesbyitem.ListSalesByItemQuery;
import com.warehouse.management.application.query.listsalesbyitem.ListSalesByItemQueryHandler;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Sales History", description = "View historical sales transactions per item or variant")
@RestController
@RequestMapping("/api/v1/items/{itemId}/sales")
@RequiredArgsConstructor
public class SaleController {

    private final ListSalesByItemQueryHandler listSalesHandler;

    @Operation(
            summary = "List sales history for an item or variant",
            description = "Returns all recorded sell transactions for an item, sorted by most recent first. " +
                    "Pass `variantId` to narrow results to a specific variant.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sales history retrieved",
                    content = @Content(schema = @Schema(implementation = PagedResponse.class))),
            @ApiResponse(responseCode = "404", description = "Item or variant not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public PagedResponse<SaleResponse> listSales(
            @Parameter(description = "Item UUID") @PathVariable UUID itemId,
            @Parameter(description = "Variant UUID — omit to see all sales for the item")
            @RequestParam(required = false) UUID variantId,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Page size (max 100)") @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {

        ListSalesByItemPageResult result = listSalesHandler.handle(
                new ListSalesByItemQuery(itemId, variantId, page, size));

        List<SaleResponse> sales = result.sales().stream()
                .map(s -> new SaleResponse(s.saleId(), s.itemId(), s.variantId(), s.quantity(), s.soldAt()))
                .toList();

        return PagedResponse.success(sales, page, size, result.totalSales());
    }
}
