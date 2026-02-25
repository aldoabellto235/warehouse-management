package com.warehouse.management.application.query.getstock;

import com.warehouse.management.domain.exception.ItemNotFoundException;
import com.warehouse.management.domain.exception.StockNotFoundException;
import com.warehouse.management.domain.model.Stock;
import com.warehouse.management.domain.repository.ItemRepository;
import com.warehouse.management.domain.repository.StockRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetStockQueryHandlerTest {

    @Mock private ItemRepository itemRepository;
    @Mock private StockRepository stockRepository;
    @InjectMocks private GetStockQueryHandler handler;

    @Test
    void should_return_stock_result_when_found() {
        UUID itemId = UUID.randomUUID();
        Stock stock = Stock.create(itemId, null);
        stock.add(42);

        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(stockRepository.findByItemIdAndVariantId(itemId, null)).thenReturn(Optional.of(stock));

        GetStockResult result = handler.handle(new GetStockQuery(itemId, null));

        assertThat(result.quantity()).isEqualTo(42);
        assertThat(result.available()).isTrue();
        assertThat(result.itemId()).isEqualTo(itemId);
    }

    @Test
    void should_return_unavailable_when_quantity_is_zero() {
        UUID itemId = UUID.randomUUID();
        Stock stock = Stock.create(itemId, null); // quantity = 0

        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(stockRepository.findByItemIdAndVariantId(itemId, null)).thenReturn(Optional.of(stock));

        GetStockResult result = handler.handle(new GetStockQuery(itemId, null));

        assertThat(result.quantity()).isZero();
        assertThat(result.available()).isFalse();
    }

    @Test
    void should_throw_when_item_not_found() {
        UUID itemId = UUID.randomUUID();
        when(itemRepository.existsById(itemId)).thenReturn(false);

        assertThatThrownBy(() -> handler.handle(new GetStockQuery(itemId, null)))
                .isInstanceOf(ItemNotFoundException.class);

        verify(stockRepository, never()).findByItemIdAndVariantId(any(), any());
    }

    @Test
    void should_throw_when_stock_record_not_found() {
        UUID itemId = UUID.randomUUID();
        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(stockRepository.findByItemIdAndVariantId(itemId, null)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(new GetStockQuery(itemId, null)))
                .isInstanceOf(StockNotFoundException.class);
    }

    @Test
    void should_return_variant_level_stock_when_variant_id_provided() {
        UUID itemId = UUID.randomUUID();
        UUID variantId = UUID.randomUUID();
        Stock stock = Stock.create(itemId, variantId);
        stock.add(7);

        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(stockRepository.findByItemIdAndVariantId(itemId, variantId)).thenReturn(Optional.of(stock));

        GetStockResult result = handler.handle(new GetStockQuery(itemId, variantId));

        assertThat(result.variantId()).isEqualTo(variantId);
        assertThat(result.quantity()).isEqualTo(7);
    }
}
