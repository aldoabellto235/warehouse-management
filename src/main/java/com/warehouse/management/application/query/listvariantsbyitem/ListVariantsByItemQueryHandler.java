package com.warehouse.management.application.query.listvariantsbyitem;

import com.warehouse.management.domain.exception.ItemNotFoundException;
import com.warehouse.management.domain.repository.ItemRepository;
import com.warehouse.management.domain.repository.ItemVariantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ListVariantsByItemQueryHandler {

    private final ItemRepository itemRepository;
    private final ItemVariantRepository variantRepository;

    @Transactional(readOnly = true)
    public List<ListVariantsByItemResult> handle(ListVariantsByItemQuery query) {
        log.debug("Handling ListVariantsByItemQuery: itemId={}", query.itemId());

        if (!itemRepository.existsById(query.itemId())) {
            throw new ItemNotFoundException(query.itemId());
        }

        return variantRepository.findByItemId(query.itemId())
                .stream()
                .map(v -> new ListVariantsByItemResult(
                        v.getId(),
                        v.getItemId(),
                        v.getName(),
                        v.getSku(),
                        v.getPrice(),
                        v.getAttributes(),
                        v.getCreatedAt(),
                        v.getUpdatedAt()
                ))
                .toList();
    }
}
