package com.warehouse.management.api.rest.v1;

import com.warehouse.management.api.exception.ErrorResponse;
import com.warehouse.management.api.rest.v1.request.AdjustStockRequest;
import com.warehouse.management.api.rest.v1.request.SellItemRequest;
import com.warehouse.management.api.rest.v1.response.ApiResponse;
import com.warehouse.management.api.rest.v1.response.StockResponse;
import com.warehouse.management.application.command.adjuststock.AdjustStockCommand;
import com.warehouse.management.application.command.adjuststock.AdjustStockCommandHandler;
import com.warehouse.management.application.command.sellitem.SellItemCommand;
import com.warehouse.management.application.command.sellitem.SellItemCommandHandler;
import com.warehouse.management.application.query.getstock.GetStockQuery;
import com.warehouse.management.application.query.getstock.GetStockQueryHandler;
import com.warehouse.management.application.query.getstock.GetStockResult;
import com.warehouse.management.application.query.liststocks.ListStocksQuery;
import com.warehouse.management.application.query.liststocks.ListStocksQueryHandler;
import com.warehouse.management.application.query.liststocks.ListStocksResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Stock", description = "Manage inventory levels — restock, deduct, and sell")
@RestController
@RequestMapping("/api/v1/items/{itemId}/stock")
@RequiredArgsConstructor
public class StockController {

    private final AdjustStockCommandHandler adjustStockHandler;
    private final SellItemCommandHandler sellItemHandler;
    private final GetStockQueryHandler getStockHandler;
    private final ListStocksQueryHandler listStocksHandler;

    @Operation(summary = "List all stock entries for an item",
            description = "Returns item-level stock and all variant-level stock entries.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Stock entries retrieved",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Item not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ApiResponse<List<StockResponse>> listStocks(
            @Parameter(description = "Item UUID") @PathVariable UUID itemId) {
        List<StockResponse> stocks = listStocksHandler.handle(new ListStocksQuery(itemId))
                .stream()
                .map(this::toStockResponse)
                .toList();
        return ApiResponse.success(stocks);
    }

    @Operation(summary = "Get stock level for a specific item or variant",
            description = "Pass variantId to query variant-level stock, omit for item-level stock.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Stock level retrieved",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Item or stock record not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/level")
    public ApiResponse<StockResponse> getStock(
            @Parameter(description = "Item UUID") @PathVariable UUID itemId,
            @Parameter(description = "Variant UUID — omit for item-level stock")
            @RequestParam(required = false) UUID variantId) {
        GetStockResult result = getStockHandler.handle(new GetStockQuery(itemId, variantId));
        return ApiResponse.success(toStockResponse(result));
    }

    @Operation(summary = "Adjust stock quantity",
            description = "Use a positive delta to restock, negative to manually deduct. " +
                    "Fails with 422 if deduction would result in negative stock.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Stock adjusted"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Item or variant not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Insufficient stock for deduction",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/adjust")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void adjustStock(
            @Parameter(description = "Item UUID") @PathVariable UUID itemId,
            @RequestBody @Valid AdjustStockRequest request) {
        adjustStockHandler.handle(
                new AdjustStockCommand(itemId, request.variantId(), request.delta()));
    }

    @Operation(summary = "Sell an item — deducts stock",
            description = "Prevents selling when stock is insufficient. Returns 422 if out of stock.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Sale processed, stock deducted"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Item or variant not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Insufficient stock — cannot sell",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/sell")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void sell(
            @Parameter(description = "Item UUID") @PathVariable UUID itemId,
            @RequestBody @Valid SellItemRequest request) {
        sellItemHandler.handle(
                new SellItemCommand(itemId, request.variantId(), request.quantity()));
    }

    // ── Mappers ──────────────────────────────────────────────────────────────

    private StockResponse toStockResponse(GetStockResult r) {
        return new StockResponse(r.stockId(), r.itemId(), r.variantId(), r.quantity(), r.available());
    }

    private StockResponse toStockResponse(ListStocksResult r) {
        return new StockResponse(r.stockId(), r.itemId(), r.variantId(), r.quantity(), r.available());
    }
}
