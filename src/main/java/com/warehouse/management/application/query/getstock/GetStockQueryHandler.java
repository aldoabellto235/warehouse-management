package com.warehouse.management.application.query.getstock;

import com.warehouse.management.domain.exception.ItemNotFoundException;
import com.warehouse.management.domain.exception.StockNotFoundException;
import com.warehouse.management.domain.model.Stock;
import com.warehouse.management.domain.repository.ItemRepository;
import com.warehouse.management.domain.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetStockQueryHandler {

    private final ItemRepository itemRepository;
    private final StockRepository stockRepository;

    @Transactional(readOnly = true)
    public GetStockResult handle(GetStockQuery query) {
        log.debug("Handling GetStockQuery: itemId={}, variantId={}", query.itemId(), query.variantId());

        if (!itemRepository.existsById(query.itemId())) {
            throw new ItemNotFoundException(query.itemId());
        }

        Stock stock = stockRepository
                .findByItemIdAndVariantId(query.itemId(), query.variantId())
                .orElseThrow(() -> new StockNotFoundException(query.itemId(), query.variantId()));

        return new GetStockResult(
                stock.getId(),
                stock.getItemId(),
                stock.getVariantId(),
                stock.getQuantity(),
                stock.getQuantity() > 0
        );
    }
}
