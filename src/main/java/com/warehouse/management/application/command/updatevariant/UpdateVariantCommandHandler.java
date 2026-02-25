package com.warehouse.management.application.command.updatevariant;

import com.warehouse.management.domain.exception.DuplicateSkuException;
import com.warehouse.management.domain.exception.VariantNotFoundException;
import com.warehouse.management.domain.model.ItemVariant;
import com.warehouse.management.domain.repository.ItemVariantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateVariantCommandHandler {

    private final ItemVariantRepository variantRepository;

    @Transactional
    public void handle(UpdateVariantCommand command) {
        log.debug("Handling UpdateVariantCommand: variantId={}", command.variantId());

        ItemVariant variant = variantRepository.findById(command.variantId())
                .orElseThrow(() -> new VariantNotFoundException(command.variantId()));

        String normalizedSku = command.sku().trim().toUpperCase();
        if (variantRepository.existsBySkuAndIdNot(normalizedSku, command.variantId())) {
            throw new DuplicateSkuException(command.sku());
        }

        variant.update(command.name(), command.sku(), command.price(), command.attributes());
        variantRepository.save(variant);

        log.debug("Variant updated: id={}", command.variantId());
    }
}
