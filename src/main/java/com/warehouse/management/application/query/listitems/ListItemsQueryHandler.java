package com.warehouse.management.application.query.listitems;

import com.warehouse.management.domain.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ListItemsQueryHandler {

    private final ItemRepository itemRepository;

    @Transactional(readOnly = true)
    public ListItemsPageResult handle(ListItemsQuery query) {
        log.debug("Handling ListItemsQuery: name={}, inStockOnly={}, page={}, size={}",
                query.name(), query.inStockOnly(), query.page(), query.size());

        boolean hasFilter = (query.name() != null && !query.name().isBlank())
                || Boolean.TRUE.equals(query.inStockOnly());

        List<ListItemsResult> items;
        long total;

        if (hasFilter) {
            boolean inStockOnly = Boolean.TRUE.equals(query.inStockOnly());
            String name = (query.name() != null && !query.name().isBlank()) ? query.name().trim() : null;

            items = itemRepository.search(name, inStockOnly, query.page(), query.size())
                    .stream()
                    .map(item -> new ListItemsResult(
                            item.getId(), item.getName(), item.getDescription(),
                            item.getBasePrice(), item.getCreatedAt(), item.getUpdatedAt()))
                    .toList();
            total = itemRepository.countSearch(name, inStockOnly);
        } else {
            items = itemRepository.findAll(query.page(), query.size())
                    .stream()
                    .map(item -> new ListItemsResult(
                            item.getId(), item.getName(), item.getDescription(),
                            item.getBasePrice(), item.getCreatedAt(), item.getUpdatedAt()))
                    .toList();
            total = itemRepository.count();
        }

        return new ListItemsPageResult(items, total);
    }
}
