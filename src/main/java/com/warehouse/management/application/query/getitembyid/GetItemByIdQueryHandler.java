package com.warehouse.management.application.query.getitembyid;

import com.warehouse.management.domain.exception.ItemNotFoundException;
import com.warehouse.management.domain.model.Item;
import com.warehouse.management.domain.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetItemByIdQueryHandler {

    private final ItemRepository itemRepository;

    @Transactional(readOnly = true)
    public GetItemByIdResult handle(GetItemByIdQuery query) {
        log.debug("Handling GetItemByIdQuery: itemId={}", query.itemId());

        Item item = itemRepository.findById(query.itemId())
                .orElseThrow(() -> new ItemNotFoundException(query.itemId()));

        return new GetItemByIdResult(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getBasePrice(),
                item.getCreatedAt(),
                item.getUpdatedAt()
        );
    }
}
