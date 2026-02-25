package com.warehouse.management.application.command.sellitem;

import com.warehouse.management.domain.exception.InsufficientStockException;
import com.warehouse.management.domain.exception.ItemNotFoundException;
import com.warehouse.management.domain.model.Stock;
import com.warehouse.management.domain.repository.ItemRepository;
import com.warehouse.management.domain.repository.ItemVariantRepository;
import com.warehouse.management.domain.repository.StockRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SellItemCommandHandlerTest {

    @Mock private ItemRepository itemRepository;
    @Mock private ItemVariantRepository variantRepository;
    @Mock private StockRepository stockRepository;

    @InjectMocks
    private SellItemCommandHandler handler;

    @Test
    void should_throw_when_item_not_found() {
        UUID itemId = UUID.randomUUID();
        when(itemRepository.existsById(itemId)).thenReturn(false);

        assertThatThrownBy(() -> handler.handle(new SellItemCommand(itemId, null, 1)))
                .isInstanceOf(ItemNotFoundException.class);
    }

    @Test
    void should_throw_when_stock_is_insufficient() {
        UUID itemId = UUID.randomUUID();
        Stock stock = Stock.create(itemId, null);
        stock.add(2);

        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(stockRepository.findByItemIdAndVariantId(itemId, null)).thenReturn(Optional.of(stock));

        assertThatThrownBy(() -> handler.handle(new SellItemCommand(itemId, null, 5)))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessageContaining("Insufficient stock");
    }

    @Test
    void should_deduct_stock_when_sufficient_quantity_available() {
        UUID itemId = UUID.randomUUID();
        Stock stock = Stock.create(itemId, null);
        stock.add(10);

        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(stockRepository.findByItemIdAndVariantId(itemId, null)).thenReturn(Optional.of(stock));

        handler.handle(new SellItemCommand(itemId, null, 3));

        verify(stockRepository).save(stock);
        // remaining quantity enforced inside domain — tested in StockTest
    }

    @Test
    void should_throw_when_quantity_is_zero_or_negative() {
        UUID itemId = UUID.randomUUID();
        when(itemRepository.existsById(any())).thenReturn(false); // won't reach this

        assertThatThrownBy(() -> handler.handle(new SellItemCommand(itemId, null, 0)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("positive");
    }
}
