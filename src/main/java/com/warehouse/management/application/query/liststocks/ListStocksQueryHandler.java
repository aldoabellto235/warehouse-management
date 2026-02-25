package com.warehouse.management.application.query.liststocks;

import com.warehouse.management.domain.exception.ItemNotFoundException;
import com.warehouse.management.domain.repository.ItemRepository;
import com.warehouse.management.domain.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ListStocksQueryHandler {

    private final ItemRepository itemRepository;
    private final StockRepository stockRepository;

    @Transactional(readOnly = true)
    public List<ListStocksResult> handle(ListStocksQuery query) {
        log.debug("Handling ListStocksQuery: itemId={}", query.itemId());

        if (!itemRepository.existsById(query.itemId())) {
            throw new ItemNotFoundException(query.itemId());
        }

        return stockRepository.findByItemId(query.itemId())
                .stream()
                .map(s -> new ListStocksResult(
                        s.getId(),
                        s.getItemId(),
                        s.getVariantId(),
                        s.getQuantity(),
                        s.getQuantity() > 0
                ))
                .toList();
    }
}
