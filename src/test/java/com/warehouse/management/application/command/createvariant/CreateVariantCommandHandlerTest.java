package com.warehouse.management.application.command.createvariant;

import com.warehouse.management.domain.exception.DuplicateSkuException;
import com.warehouse.management.domain.exception.ItemNotFoundException;
import com.warehouse.management.domain.repository.ItemRepository;
import com.warehouse.management.domain.repository.ItemVariantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateVariantCommandHandlerTest {

    @Mock private ItemRepository itemRepository;
    @Mock private ItemVariantRepository variantRepository;
    @InjectMocks private CreateVariantCommandHandler handler;

    @Test
    void should_create_variant_and_return_id_when_valid() {
        UUID itemId = UUID.randomUUID();
        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(variantRepository.existsBySku("TSHIRT-RED-L")).thenReturn(false);

        UUID id = handler.handle(new CreateVariantCommand(itemId, "Red / Large", "TSHIRT-RED-L",
                new BigDecimal("29.99"), Map.of("color", "red")));

        assertThat(id).isNotNull();
        verify(variantRepository).save(any());
    }

    @Test
    void should_throw_when_item_not_found() {
        UUID itemId = UUID.randomUUID();
        when(itemRepository.existsById(itemId)).thenReturn(false);

        assertThatThrownBy(() -> handler.handle(
                new CreateVariantCommand(itemId, "Red", "SKU-001", new BigDecimal("10.00"), null)))
                .isInstanceOf(ItemNotFoundException.class);

        verify(variantRepository, never()).save(any());
    }

    @Test
    void should_throw_when_sku_already_exists() {
        UUID itemId = UUID.randomUUID();
        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(variantRepository.existsBySku("TSHIRT-RED-L")).thenReturn(true);

        assertThatThrownBy(() -> handler.handle(
                new CreateVariantCommand(itemId, "Red", "TSHIRT-RED-L", new BigDecimal("10.00"), null)))
                .isInstanceOf(DuplicateSkuException.class);

        verify(variantRepository, never()).save(any());
    }

    @Test
    void should_normalize_sku_to_uppercase_before_checking_duplicate() {
        UUID itemId = UUID.randomUUID();
        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(variantRepository.existsBySku("LOWERCASE-SKU")).thenReturn(false);

        handler.handle(new CreateVariantCommand(itemId, "Item", "lowercase-sku",
                new BigDecimal("10.00"), null));

        verify(variantRepository).existsBySku("LOWERCASE-SKU");
    }
}
