package com.warehouse.management.domain.model;

import com.warehouse.management.domain.exception.InsufficientStockException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class StockTest {

    private final UUID itemId = UUID.randomUUID();

    @Test
    void should_start_with_zero_quantity() {
        Stock stock = Stock.create(itemId, null);
        assertThat(stock.getQuantity()).isZero();
    }

    @Test
    void should_add_stock_when_amount_is_positive() {
        Stock stock = Stock.create(itemId, null);
        stock.add(50);
        assertThat(stock.getQuantity()).isEqualTo(50);
    }

    @Test
    void should_deduct_stock_when_sufficient_quantity_available() {
        Stock stock = Stock.create(itemId, null);
        stock.add(100);
        stock.deduct(30);
        assertThat(stock.getQuantity()).isEqualTo(70);
    }

    @Test
    void should_throw_when_deducting_more_than_available() {
        Stock stock = Stock.create(itemId, null);
        stock.add(10);

        assertThatThrownBy(() -> stock.deduct(11))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessageContaining("Insufficient stock");
    }

    @Test
    void should_throw_when_adding_zero_or_negative_amount() {
        Stock stock = Stock.create(itemId, null);

        assertThatIllegalArgumentException().isThrownBy(() -> stock.add(0));
        assertThatIllegalArgumentException().isThrownBy(() -> stock.add(-5));
    }

    @Test
    void should_return_false_for_availability_when_out_of_stock() {
        Stock stock = Stock.create(itemId, null);
        assertThat(stock.isAvailable(1)).isFalse();
    }

    @Test
    void should_return_true_for_availability_when_stock_is_sufficient() {
        Stock stock = Stock.create(itemId, null);
        stock.add(5);
        assertThat(stock.isAvailable(5)).isTrue();
        assertThat(stock.isAvailable(6)).isFalse();
    }
}
