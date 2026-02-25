package com.warehouse.management.application.query.listsalesbyitem;

import com.warehouse.management.domain.exception.ItemNotFoundException;
import com.warehouse.management.domain.exception.VariantNotFoundException;
import com.warehouse.management.domain.model.Sale;
import com.warehouse.management.domain.repository.ItemRepository;
import com.warehouse.management.domain.repository.ItemVariantRepository;
import com.warehouse.management.domain.repository.SaleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListSalesByItemQueryHandlerTest {

    @Mock private ItemRepository itemRepository;
    @Mock private ItemVariantRepository variantRepository;
    @Mock private SaleRepository saleRepository;
    @InjectMocks private ListSalesByItemQueryHandler handler;

    @Test
    void should_return_item_sales_when_no_variant_filter() {
        UUID itemId = UUID.randomUUID();
        Sale sale = Sale.record(itemId, null, 2);

        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(saleRepository.findByItemId(itemId, 0, 20)).thenReturn(List.of(sale));
        when(saleRepository.countByItemId(itemId)).thenReturn(1L);

        ListSalesByItemPageResult result = handler.handle(new ListSalesByItemQuery(itemId, null, 0, 20));

        assertThat(result.sales()).hasSize(1);
        assertThat(result.sales().get(0).quantity()).isEqualTo(2);
        assertThat(result.totalSales()).isEqualTo(1L);
        verify(saleRepository).findByItemId(itemId, 0, 20);
    }

    @Test
    void should_return_variant_sales_when_variant_id_provided() {
        UUID itemId = UUID.randomUUID();
        UUID variantId = UUID.randomUUID();
        Sale sale = Sale.record(itemId, variantId, 3);

        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(variantRepository.findById(variantId)).thenReturn(Optional.of(
                com.warehouse.management.domain.model.ItemVariant.create(itemId, "Red", "SKU", new java.math.BigDecimal("10"), null)));
        when(saleRepository.findByVariantId(variantId, 0, 20)).thenReturn(List.of(sale));
        when(saleRepository.countByVariantId(variantId)).thenReturn(1L);

        ListSalesByItemPageResult result = handler.handle(new ListSalesByItemQuery(itemId, variantId, 0, 20));

        assertThat(result.sales()).hasSize(1);
        assertThat(result.sales().get(0).variantId()).isEqualTo(variantId);
        verify(saleRepository).findByVariantId(variantId, 0, 20);
        verify(saleRepository, never()).findByItemId(any(), anyInt(), anyInt());
    }

    @Test
    void should_throw_when_item_not_found() {
        UUID itemId = UUID.randomUUID();
        when(itemRepository.existsById(itemId)).thenReturn(false);

        assertThatThrownBy(() -> handler.handle(new ListSalesByItemQuery(itemId, null, 0, 20)))
                .isInstanceOf(ItemNotFoundException.class);

        verify(saleRepository, never()).findByItemId(any(), anyInt(), anyInt());
    }

    @Test
    void should_throw_when_variant_not_found() {
        UUID itemId = UUID.randomUUID();
        UUID variantId = UUID.randomUUID();
        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(variantRepository.findById(variantId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(new ListSalesByItemQuery(itemId, variantId, 0, 20)))
                .isInstanceOf(VariantNotFoundException.class);

        verify(saleRepository, never()).findByVariantId(any(), anyInt(), anyInt());
    }

    @Test
    void should_return_empty_when_no_sales() {
        UUID itemId = UUID.randomUUID();
        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(saleRepository.findByItemId(itemId, 0, 20)).thenReturn(List.of());
        when(saleRepository.countByItemId(itemId)).thenReturn(0L);

        ListSalesByItemPageResult result = handler.handle(new ListSalesByItemQuery(itemId, null, 0, 20));

        assertThat(result.sales()).isEmpty();
        assertThat(result.totalSales()).isZero();
    }
}
