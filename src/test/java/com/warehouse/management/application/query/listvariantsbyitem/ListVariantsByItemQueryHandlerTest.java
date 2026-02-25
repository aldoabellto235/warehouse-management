package com.warehouse.management.application.query.listvariantsbyitem;

import com.warehouse.management.domain.exception.ItemNotFoundException;
import com.warehouse.management.domain.model.ItemVariant;
import com.warehouse.management.domain.repository.ItemRepository;
import com.warehouse.management.domain.repository.ItemVariantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListVariantsByItemQueryHandlerTest {

    @Mock private ItemRepository itemRepository;
    @Mock private ItemVariantRepository variantRepository;
    @InjectMocks private ListVariantsByItemQueryHandler handler;

    @Test
    void should_return_variants_when_item_exists() {
        UUID itemId = UUID.randomUUID();
        ItemVariant v1 = ItemVariant.create(itemId, "Red / Large", "SKU-RED-L", new BigDecimal("29.99"), null);
        ItemVariant v2 = ItemVariant.create(itemId, "Blue / Medium", "SKU-BLUE-M", new BigDecimal("29.99"), null);

        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(variantRepository.findByItemId(itemId)).thenReturn(List.of(v1, v2));

        List<ListVariantsByItemResult> results = handler.handle(new ListVariantsByItemQuery(itemId));

        assertThat(results).hasSize(2);
        assertThat(results).extracting(ListVariantsByItemResult::name)
                .containsExactlyInAnyOrder("Red / Large", "Blue / Medium");
    }

    @Test
    void should_return_empty_list_when_item_has_no_variants() {
        UUID itemId = UUID.randomUUID();
        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(variantRepository.findByItemId(itemId)).thenReturn(List.of());

        List<ListVariantsByItemResult> results = handler.handle(new ListVariantsByItemQuery(itemId));

        assertThat(results).isEmpty();
    }

    @Test
    void should_throw_when_item_not_found() {
        UUID itemId = UUID.randomUUID();
        when(itemRepository.existsById(itemId)).thenReturn(false);

        assertThatThrownBy(() -> handler.handle(new ListVariantsByItemQuery(itemId)))
                .isInstanceOf(ItemNotFoundException.class);

        verify(variantRepository, never()).findByItemId(any());
    }
}
