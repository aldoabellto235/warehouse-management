package com.warehouse.management.application.command.updateitem;

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
public class UpdateItemCommandHandler {

    private final ItemRepository itemRepository;

    @Transactional
    public void handle(UpdateItemCommand command) {
        log.debug("Handling UpdateItemCommand: itemId={}", command.itemId());

        Item item = itemRepository.findById(command.itemId())
                .orElseThrow(() -> new ItemNotFoundException(command.itemId()));

        item.update(command.name(), command.description(), command.basePrice());
        itemRepository.save(item);

        log.debug("Item updated: id={}", command.itemId());
    }
}
