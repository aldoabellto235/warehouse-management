package com.warehouse.management.application.query.getitemsummary;

import com.warehouse.management.domain.exception.ItemNotFoundException;
import com.warehouse.management.domain.model.Item;
import com.warehouse.management.domain.model.ItemVariant;
import com.warehouse.management.domain.model.Stock;
import com.warehouse.management.domain.repository.ItemRepository;
import com.warehouse.management.domain.repository.ItemVariantRepository;
import com.warehouse.management.domain.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetItemSummaryQueryHandler {

    private final ItemRepository itemRepository;
    private final ItemVariantRepository variantRepository;
    private final StockRepository stockRepository;

    @Transactional(readOnly = true)
    public GetItemSummaryResult handle(GetItemSummaryQuery query) {
        log.debug("Handling GetItemSummaryQuery: itemId={}", query.itemId());

        Item item = itemRepository.findById(query.itemId())
                .orElseThrow(() -> new ItemNotFoundException(query.itemId()));

        List<ItemVariant> variants = variantRepository.findByItemId(query.itemId());
        boolean hasVariants = !variants.isEmpty();

        if (hasVariants) {
            List<GetItemSummaryResult.VariantStock> variantStocks = variants.stream()
                    .map(v -> {
                        int qty = stockRepository
                                .findByItemIdAndVariantId(item.getId(), v.getId())
                                .map(Stock::getQuantity)
                                .orElse(0);
                        return new GetItemSummaryResult.VariantStock(
                                v.getId(), v.getName(), v.getSku(),
                                v.getPrice(), v.getAttributes(), qty, qty > 0);
                    })
                    .toList();

            int totalStock = variantStocks.stream().mapToInt(GetItemSummaryResult.VariantStock::stock).sum();

            return new GetItemSummaryResult(
                    item.getId(), item.getName(), item.getDescription(),
                    item.getBasePrice(), true, totalStock,
                    variantStocks, item.getCreatedAt(), item.getUpdatedAt());
        } else {
            int qty = stockRepository
                    .findByItemIdAndVariantId(item.getId(), null)
                    .map(Stock::getQuantity)
                    .orElse(0);

            return new GetItemSummaryResult(
                    item.getId(), item.getName(), item.getDescription(),
                    item.getBasePrice(), false, qty,
                    List.of(), item.getCreatedAt(), item.getUpdatedAt());
        }
    }
}
