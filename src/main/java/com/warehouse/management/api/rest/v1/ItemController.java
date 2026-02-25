package com.warehouse.management.api.rest.v1;

import com.warehouse.management.api.exception.ErrorResponse;
import com.warehouse.management.api.rest.v1.request.CreateItemRequest;
import com.warehouse.management.api.rest.v1.request.CreateVariantRequest;
import com.warehouse.management.api.rest.v1.request.UpdateItemRequest;
import com.warehouse.management.api.rest.v1.request.UpdateVariantRequest;
import com.warehouse.management.api.rest.v1.response.ApiResponse;
import com.warehouse.management.api.rest.v1.response.CreatedResponse;
import com.warehouse.management.api.rest.v1.response.ItemResponse;
import com.warehouse.management.api.rest.v1.response.PagedResponse;
import com.warehouse.management.api.rest.v1.response.ItemSummaryResponse;
import com.warehouse.management.api.rest.v1.response.VariantResponse;
import com.warehouse.management.application.command.createitem.CreateItemCommand;
import com.warehouse.management.application.command.createitem.CreateItemCommandHandler;
import com.warehouse.management.application.command.createvariant.CreateVariantCommand;
import com.warehouse.management.application.command.createvariant.CreateVariantCommandHandler;
import com.warehouse.management.application.command.deleteitem.DeleteItemCommand;
import com.warehouse.management.application.command.deleteitem.DeleteItemCommandHandler;
import com.warehouse.management.application.command.deletevariant.DeleteVariantCommand;
import com.warehouse.management.application.command.deletevariant.DeleteVariantCommandHandler;
import com.warehouse.management.application.command.updateitem.UpdateItemCommand;
import com.warehouse.management.application.command.updateitem.UpdateItemCommandHandler;
import com.warehouse.management.application.command.updatevariant.UpdateVariantCommand;
import com.warehouse.management.application.command.updatevariant.UpdateVariantCommandHandler;
import com.warehouse.management.application.query.getitembyid.GetItemByIdQuery;
import com.warehouse.management.application.query.getitembyid.GetItemByIdQueryHandler;
import com.warehouse.management.application.query.getitembyid.GetItemByIdResult;
import com.warehouse.management.application.query.listitems.ListItemsPageResult;
import com.warehouse.management.application.query.listitems.ListItemsQuery;
import com.warehouse.management.application.query.listitems.ListItemsQueryHandler;
import com.warehouse.management.application.query.getitemsummary.GetItemSummaryQuery;
import com.warehouse.management.application.query.getitemsummary.GetItemSummaryQueryHandler;
import com.warehouse.management.application.query.getitemsummary.GetItemSummaryResult;
import com.warehouse.management.application.query.listvariantsbyitem.ListVariantsByItemQuery;
import com.warehouse.management.application.query.listvariantsbyitem.ListVariantsByItemQueryHandler;
import com.warehouse.management.application.query.listvariantsbyitem.ListVariantsByItemResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Items", description = "Manage items and their variants")
@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
public class ItemController {

    private final CreateItemCommandHandler createItemHandler;
    private final UpdateItemCommandHandler updateItemHandler;
    private final DeleteItemCommandHandler deleteItemHandler;
    private final CreateVariantCommandHandler createVariantHandler;
    private final UpdateVariantCommandHandler updateVariantHandler;
    private final DeleteVariantCommandHandler deleteVariantHandler;
    private final GetItemByIdQueryHandler getItemByIdHandler;
    private final GetItemSummaryQueryHandler getItemSummaryHandler;
    private final ListItemsQueryHandler listItemsHandler;
    private final ListVariantsByItemQueryHandler listVariantsHandler;

    // ── Items ────────────────────────────────────────────────────────────────

    @Operation(summary = "Create a new item")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Item created",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CreatedResponse> createItem(@RequestBody @Valid CreateItemRequest request) {
        UUID id = createItemHandler.handle(
                new CreateItemCommand(request.name(), request.description(), request.basePrice()));
        return ApiResponse.success(new CreatedResponse(id));
    }

    @Operation(summary = "List / search items (paginated)",
            description = "Returns all items. Use `name` for case-insensitive search and `inStockOnly=true` to hide out-of-stock items.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Items retrieved",
            content = @Content(schema = @Schema(implementation = PagedResponse.class)))
    @GetMapping
    public PagedResponse<ItemResponse> listItems(
            @Parameter(description = "Filter by name (case-insensitive, partial match)") @RequestParam(required = false) String name,
            @Parameter(description = "When true, only items with stock > 0 are returned") @RequestParam(required = false) Boolean inStockOnly,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Page size (max 100)") @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        ListItemsPageResult result = listItemsHandler.handle(new ListItemsQuery(name, inStockOnly, page, size));
        List<ItemResponse> items = result.items().stream().map(this::toItemResponse).toList();
        return PagedResponse.success(items, page, size, result.totalItems());
    }

    @Operation(summary = "Get item by ID")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Item found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Item not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ApiResponse<ItemResponse> getItem(
            @Parameter(description = "Item UUID") @PathVariable UUID id) {
        GetItemByIdResult result = getItemByIdHandler.handle(new GetItemByIdQuery(id));
        return ApiResponse.success(new ItemResponse(result.id(), result.name(), result.description(),
                result.basePrice(), result.createdAt(), result.updatedAt()));
    }

    @Operation(summary = "Get item summary — item details + variants + stock levels",
            description = "Returns the item, all its variants, and the current stock for each. " +
                    "For items without variants, shows the item-level stock directly.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Summary retrieved",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Item not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}/summary")
    public ApiResponse<ItemSummaryResponse> getItemSummary(
            @Parameter(description = "Item UUID") @PathVariable UUID id) {
        GetItemSummaryResult r = getItemSummaryHandler.handle(new GetItemSummaryQuery(id));

        List<ItemSummaryResponse.VariantStockResponse> variants = r.variants().stream()
                .map(v -> new ItemSummaryResponse.VariantStockResponse(
                        v.variantId(), v.name(), v.sku(), v.price(), v.attributes(), v.stock(), v.available()))
                .toList();

        return ApiResponse.success(new ItemSummaryResponse(
                r.id(), r.name(), r.description(), r.basePrice(),
                r.hasVariants(), r.totalStock(), variants, r.createdAt(), r.updatedAt()));
    }

    @Operation(summary = "Update an item")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Item updated"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Item not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateItem(
            @Parameter(description = "Item UUID") @PathVariable UUID id,
            @RequestBody @Valid UpdateItemRequest request) {
        updateItemHandler.handle(
                new UpdateItemCommand(id, request.name(), request.description(), request.basePrice()));
    }

    @Operation(summary = "Delete an item and all its variants and stock")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Item deleted"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Item not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(
            @Parameter(description = "Item UUID") @PathVariable UUID id) {
        deleteItemHandler.handle(new DeleteItemCommand(id));
    }

    // ── Variants ─────────────────────────────────────────────────────────────

    @Operation(summary = "Add a variant to an item")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Variant created",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Item not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Duplicate SKU",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{itemId}/variants")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CreatedResponse> createVariant(
            @Parameter(description = "Item UUID") @PathVariable UUID itemId,
            @RequestBody @Valid CreateVariantRequest request) {
        UUID variantId = createVariantHandler.handle(
                new CreateVariantCommand(itemId, request.name(), request.sku(),
                        request.price(), request.attributes()));
        return ApiResponse.success(new CreatedResponse(variantId));
    }

    @Operation(summary = "List all variants for an item")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Variants retrieved",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Item not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{itemId}/variants")
    public ApiResponse<List<VariantResponse>> listVariants(
            @Parameter(description = "Item UUID") @PathVariable UUID itemId) {
        List<VariantResponse> variants = listVariantsHandler.handle(new ListVariantsByItemQuery(itemId))
                .stream()
                .map(this::toVariantResponse)
                .toList();
        return ApiResponse.success(variants);
    }

    @Operation(summary = "Update a variant")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Variant updated"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Variant not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Duplicate SKU",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{itemId}/variants/{variantId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateVariant(
            @Parameter(description = "Item UUID") @PathVariable UUID itemId,
            @Parameter(description = "Variant UUID") @PathVariable UUID variantId,
            @RequestBody @Valid UpdateVariantRequest request) {
        updateVariantHandler.handle(
                new UpdateVariantCommand(variantId, request.name(), request.sku(),
                        request.price(), request.attributes()));
    }

    @Operation(summary = "Delete a variant")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Variant deleted"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Variant not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{itemId}/variants/{variantId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVariant(
            @Parameter(description = "Item UUID") @PathVariable UUID itemId,
            @Parameter(description = "Variant UUID") @PathVariable UUID variantId) {
        deleteVariantHandler.handle(new DeleteVariantCommand(variantId));
    }

    // ── Mappers ──────────────────────────────────────────────────────────────

    private ItemResponse toItemResponse(com.warehouse.management.application.query.listitems.ListItemsResult r) {
        return new ItemResponse(r.id(), r.name(), r.description(),
                r.basePrice(), r.createdAt(), r.updatedAt());
    }

    private VariantResponse toVariantResponse(ListVariantsByItemResult r) {
        return new VariantResponse(r.id(), r.itemId(), r.name(), r.sku(),
                r.price(), r.attributes(), r.createdAt(), r.updatedAt());
    }
}
