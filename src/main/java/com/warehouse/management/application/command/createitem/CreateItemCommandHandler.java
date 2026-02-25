package com.warehouse.management.application.command.createitem;

import com.warehouse.management.domain.model.Item;
import com.warehouse.management.domain.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateItemCommandHandler {

    private final ItemRepository itemRepository;

    @Transactional
    public UUID handle(CreateItemCommand command) {
        log.debug("Handling CreateItemCommand: name={}", command.name());

        Item item = Item.create(command.name(), command.description(), command.basePrice());
        itemRepository.save(item);

        log.debug("Item created with id={}", item.getId());
        return item.getId();
    }
}
