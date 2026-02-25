package com.warehouse.management.application.query.liststocks;

import com.warehouse.management.domain.exception.ItemNotFoundException;
import com.warehouse.management.domain.model.Stock;
import com.warehouse.management.domain.repository.ItemRepository;
import com.warehouse.management.domain.repository.StockRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListStocksQueryHandlerTest {

    @Mock private ItemRepository itemRepository;
    @Mock private StockRepository stockRepository;
    @InjectMocks private ListStocksQueryHandler handler;

    @Test
    void should_return_all_stock_entries_for_item() {
        UUID itemId = UUID.randomUUID();
        UUID variantId = UUID.randomUUID();
        Stock itemStock = Stock.create(itemId, null);
        itemStock.add(10);
        Stock variantStock = Stock.create(itemId, variantId);
        variantStock.add(5);

        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(stockRepository.findByItemId(itemId)).thenReturn(List.of(itemStock, variantStock));

        List<ListStocksResult> results = handler.handle(new ListStocksQuery(itemId));

        assertThat(results).hasSize(2);
        assertThat(results).anySatisfy(r -> assertThat(r.quantity()).isEqualTo(10));
        assertThat(results).anySatisfy(r -> assertThat(r.quantity()).isEqualTo(5));
    }

    @Test
    void should_return_empty_when_no_stock_records() {
        UUID itemId = UUID.randomUUID();
        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(stockRepository.findByItemId(itemId)).thenReturn(List.of());

        List<ListStocksResult> results = handler.handle(new ListStocksQuery(itemId));

        assertThat(results).isEmpty();
    }

    @Test
    void should_throw_when_item_not_found() {
        UUID itemId = UUID.randomUUID();
        when(itemRepository.existsById(itemId)).thenReturn(false);

        assertThatThrownBy(() -> handler.handle(new ListStocksQuery(itemId)))
                .isInstanceOf(ItemNotFoundException.class);

        verify(stockRepository, never()).findByItemId(any());
    }

    @Test
    void should_set_available_true_when_quantity_is_positive() {
        UUID itemId = UUID.randomUUID();
        Stock stock = Stock.create(itemId, null);
        stock.add(1);

        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(stockRepository.findByItemId(itemId)).thenReturn(List.of(stock));

        List<ListStocksResult> results = handler.handle(new ListStocksQuery(itemId));

        assertThat(results.get(0).available()).isTrue();
    }

    @Test
    void should_set_available_false_when_quantity_is_zero() {
        UUID itemId = UUID.randomUUID();
        Stock stock = Stock.create(itemId, null); // quantity = 0

        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(stockRepository.findByItemId(itemId)).thenReturn(List.of(stock));

        List<ListStocksResult> results = handler.handle(new ListStocksQuery(itemId));

        assertThat(results.get(0).available()).isFalse();
    }
}
