package com.warehouse.management.application.command.deletevariant;

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
class DeleteVariantCommandHandlerTest {

    @Mock private ItemVariantRepository variantRepository;
    @InjectMocks private DeleteVariantCommandHandler handler;

    @Test
    void should_delete_variant_when_exists() {
        UUID variantId = UUID.randomUUID();
        ItemVariant variant = ItemVariant.create(UUID.randomUUID(), "Red", "SKU-001",
                new BigDecimal("10.00"), null);
        when(variantRepository.findById(variantId)).thenReturn(Optional.of(variant));

        handler.handle(new DeleteVariantCommand(variantId));

        verify(variantRepository).deleteById(variantId);
    }

    @Test
    void should_throw_when_variant_not_found() {
        UUID variantId = UUID.randomUUID();
        when(variantRepository.findById(variantId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(new DeleteVariantCommand(variantId)))
                .isInstanceOf(VariantNotFoundException.class);

        verify(variantRepository, never()).deleteById(any());
    }
}
