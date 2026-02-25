package com.warehouse.management.application.command.adjuststock;

import com.warehouse.management.domain.exception.InsufficientStockException;
import com.warehouse.management.domain.exception.ItemNotFoundException;
import com.warehouse.management.domain.exception.VariantNotFoundException;
import com.warehouse.management.domain.model.ItemVariant;
import com.warehouse.management.domain.model.Stock;
import com.warehouse.management.domain.repository.ItemRepository;
import com.warehouse.management.domain.repository.ItemVariantRepository;
import com.warehouse.management.domain.repository.StockRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdjustStockCommandHandlerTest {

    @Mock private ItemRepository itemRepository;
    @Mock private ItemVariantRepository variantRepository;
    @Mock private StockRepository stockRepository;
    @InjectMocks private AdjustStockCommandHandler handler;

    @Test
    void should_add_stock_when_delta_is_positive() {
        UUID itemId = UUID.randomUUID();
        Stock stock = Stock.create(itemId, null);
        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(stockRepository.findByItemIdAndVariantId(itemId, null)).thenReturn(Optional.of(stock));

        handler.handle(new AdjustStockCommand(itemId, null, 50));

        assertThat(stock.getQuantity()).isEqualTo(50);
        verify(stockRepository).save(stock);
    }

    @Test
    void should_deduct_stock_when_delta_is_negative() {
        UUID itemId = UUID.randomUUID();
        Stock stock = Stock.create(itemId, null);
        stock.add(100);
        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(stockRepository.findByItemIdAndVariantId(itemId, null)).thenReturn(Optional.of(stock));

        handler.handle(new AdjustStockCommand(itemId, null, -30));

        assertThat(stock.getQuantity()).isEqualTo(70);
        verify(stockRepository).save(stock);
    }

    @Test
    void should_create_stock_record_when_not_exists_and_add() {
        UUID itemId = UUID.randomUUID();
        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(stockRepository.findByItemIdAndVariantId(itemId, null)).thenReturn(Optional.empty());

        handler.handle(new AdjustStockCommand(itemId, null, 20));

        ArgumentCaptor<Stock> captor = ArgumentCaptor.forClass(Stock.class);
        verify(stockRepository).save(captor.capture());
        assertThat(captor.getValue().getQuantity()).isEqualTo(20);
    }

    @Test
    void should_do_nothing_when_delta_is_zero() {
        UUID itemId = UUID.randomUUID();
        Stock stock = Stock.create(itemId, null);
        stock.add(10);
        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(stockRepository.findByItemIdAndVariantId(itemId, null)).thenReturn(Optional.of(stock));

        handler.handle(new AdjustStockCommand(itemId, null, 0));

        assertThat(stock.getQuantity()).isEqualTo(10);
        verify(stockRepository).save(stock);
    }

    @Test
    void should_throw_when_item_not_found() {
        UUID itemId = UUID.randomUUID();
        when(itemRepository.existsById(itemId)).thenReturn(false);

        assertThatThrownBy(() -> handler.handle(new AdjustStockCommand(itemId, null, 10)))
                .isInstanceOf(ItemNotFoundException.class);

        verify(stockRepository, never()).save(any());
    }

    @Test
    void should_throw_when_variant_not_found() {
        UUID itemId = UUID.randomUUID();
        UUID variantId = UUID.randomUUID();
        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(variantRepository.findById(variantId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(new AdjustStockCommand(itemId, variantId, 10)))
                .isInstanceOf(VariantNotFoundException.class);

        verify(stockRepository, never()).save(any());
    }

    @Test
    void should_throw_when_deduction_exceeds_stock() {
        UUID itemId = UUID.randomUUID();
        Stock stock = Stock.create(itemId, null);
        stock.add(5);
        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(stockRepository.findByItemIdAndVariantId(itemId, null)).thenReturn(Optional.of(stock));

        assertThatThrownBy(() -> handler.handle(new AdjustStockCommand(itemId, null, -10)))
                .isInstanceOf(InsufficientStockException.class);
    }

    @Test
    void should_handle_variant_level_stock_adjustment() {
        UUID itemId = UUID.randomUUID();
        UUID variantId = UUID.randomUUID();
        ItemVariant variant = ItemVariant.create(itemId, "Red", "SKU-001", new BigDecimal("10.00"), null);
        Stock stock = Stock.create(itemId, variantId);
        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(variantRepository.findById(variantId)).thenReturn(Optional.of(variant));
        when(stockRepository.findByItemIdAndVariantId(itemId, variantId)).thenReturn(Optional.of(stock));

        handler.handle(new AdjustStockCommand(itemId, variantId, 25));

        assertThat(stock.getQuantity()).isEqualTo(25);
        verify(stockRepository).save(stock);
    }
}
