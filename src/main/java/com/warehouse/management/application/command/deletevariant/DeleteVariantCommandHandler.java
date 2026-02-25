package com.warehouse.management.application.command.deletevariant;

import com.warehouse.management.domain.exception.VariantNotFoundException;
import com.warehouse.management.domain.repository.ItemVariantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteVariantCommandHandler {

    private final ItemVariantRepository variantRepository;

    @Transactional
    public void handle(DeleteVariantCommand command) {
        log.debug("Handling DeleteVariantCommand: variantId={}", command.variantId());

        if (variantRepository.findById(command.variantId()).isEmpty()) {
            throw new VariantNotFoundException(command.variantId());
        }

        variantRepository.deleteById(command.variantId());
        log.debug("Variant deleted: id={}", command.variantId());
    }
}
