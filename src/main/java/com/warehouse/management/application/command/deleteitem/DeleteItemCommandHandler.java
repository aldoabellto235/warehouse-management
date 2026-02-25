package com.warehouse.management.application.command.deleteitem;

import com.warehouse.management.domain.exception.ItemNotFoundException;
import com.warehouse.management.domain.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteItemCommandHandler {

    private final ItemRepository itemRepository;

    @Transactional
    public void handle(DeleteItemCommand command) {
        log.debug("Handling DeleteItemCommand: itemId={}", command.itemId());

        if (!itemRepository.existsById(command.itemId())) {
            throw new ItemNotFoundException(command.itemId());
        }

        itemRepository.deleteById(command.itemId());
        log.debug("Item deleted: id={}", command.itemId());
    }
}
