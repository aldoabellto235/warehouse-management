package com.warehouse.management.application.command.updatevariant;

import com.warehouse.management.domain.exception.DuplicateSkuException;
import com.warehouse.management.domain.exception.VariantNotFoundException;
import com.warehouse.management.domain.model.ItemVariant;
import com.warehouse.management.domain.repository.ItemVariantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateVariantCommandHandlerTest {

    @Mock private ItemVariantRepository variantRepository;
    @InjectMocks private UpdateVariantCommandHandler handler;

    @Test
    void should_update_and_save_variant_when_valid() {
        UUID variantId = UUID.randomUUID();
        ItemVariant variant = ItemVariant.create(UUID.randomUUID(), "Old Name", "OLD-SKU",
                new BigDecimal("10.00"), null);
        when(variantRepository.findById(variantId)).thenReturn(Optional.of(variant));
        when(variantRepository.existsBySkuAndIdNot("NEW-SKU", variantId)).thenReturn(false);

        handler.handle(new UpdateVariantCommand(variantId, "New Name", "NEW-SKU",
                new BigDecimal("49.99"), null));

        verify(variantRepository).save(variant);
    }

    @Test
    void should_throw_when_variant_not_found() {
        UUID variantId = UUID.randomUUID();
        when(variantRepository.findById(variantId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(
                new UpdateVariantCommand(variantId, "Name", "SKU", new BigDecimal("10.00"), null)))
                .isInstanceOf(VariantNotFoundException.class);

        verify(variantRepository, never()).save(any());
    }

    @Test
    void should_throw_when_new_sku_conflicts_with_another_variant() {
        UUID variantId = UUID.randomUUID();
        ItemVariant variant = ItemVariant.create(UUID.randomUUID(), "Name", "OLD-SKU",
                new BigDecimal("10.00"), null);
        when(variantRepository.findById(variantId)).thenReturn(Optional.of(variant));
        when(variantRepository.existsBySkuAndIdNot("CONFLICT-SKU", variantId)).thenReturn(true);

        assertThatThrownBy(() -> handler.handle(
                new UpdateVariantCommand(variantId, "Name", "CONFLICT-SKU", new BigDecimal("10.00"), null)))
                .isInstanceOf(DuplicateSkuException.class);

        verify(variantRepository, never()).save(any());
    }

    @Test
    void should_allow_keeping_own_sku_on_update() {
        UUID variantId = UUID.randomUUID();
        ItemVariant variant = ItemVariant.create(UUID.randomUUID(), "Name", "SAME-SKU",
                new BigDecimal("10.00"), null);
        when(variantRepository.findById(variantId)).thenReturn(Optional.of(variant));
        when(variantRepository.existsBySkuAndIdNot("SAME-SKU", variantId)).thenReturn(false);

        handler.handle(new UpdateVariantCommand(variantId, "Name", "SAME-SKU",
                new BigDecimal("20.00"), null));

        verify(variantRepository).save(variant);
    }
}
