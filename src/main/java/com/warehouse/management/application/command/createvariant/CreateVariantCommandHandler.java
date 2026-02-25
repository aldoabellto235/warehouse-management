package com.warehouse.management.application.command.createvariant;

import com.warehouse.management.domain.exception.DuplicateSkuException;
import com.warehouse.management.domain.exception.ItemNotFoundException;
import com.warehouse.management.domain.model.ItemVariant;
import com.warehouse.management.domain.repository.ItemRepository;
import com.warehouse.management.domain.repository.ItemVariantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateVariantCommandHandler {

    private final ItemRepository itemRepository;
    private final ItemVariantRepository variantRepository;

    @Transactional
    public UUID handle(CreateVariantCommand command) {
        log.debug("Handling CreateVariantCommand: itemId={}, sku={}", command.itemId(), command.sku());

        if (!itemRepository.existsById(command.itemId())) {
            throw new ItemNotFoundException(command.itemId());
        }

        String normalizedSku = command.sku().trim().toUpperCase();
        if (variantRepository.existsBySku(normalizedSku)) {
            throw new DuplicateSkuException(command.sku());
        }

        ItemVariant variant = ItemVariant.create(
                command.itemId(), command.name(), command.sku(),
                command.price(), command.attributes());
        variantRepository.save(variant);

        log.debug("Variant created with id={}", variant.getId());
        return variant.getId();
    }
}
