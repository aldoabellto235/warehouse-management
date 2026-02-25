package com.warehouse.management.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class SaleTest {

    private final UUID itemId = UUID.randomUUID();

    @Test
    void should_record_sale_when_valid_inputs() {
        Sale sale = Sale.record(itemId, null, 3);

        assertThat(sale.getId()).isNotNull();
        assertThat(sale.getItemId()).isEqualTo(itemId);
        assertThat(sale.getVariantId()).isNull();
        assertThat(sale.getQuantity()).isEqualTo(3);
        assertThat(sale.getSoldAt()).isNotNull();
    }

    @Test
    void should_record_sale_with_variant() {
        UUID variantId = UUID.randomUUID();
        Sale sale = Sale.record(itemId, variantId, 5);

        assertThat(sale.getVariantId()).isEqualTo(variantId);
        assertThat(sale.getQuantity()).isEqualTo(5);
    }

    @Test
    void should_throw_when_quantity_is_zero() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Sale.record(itemId, null, 0))
                .withMessageContaining("positive");
    }

    @Test
    void should_throw_when_quantity_is_negative() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Sale.record(itemId, null, -1))
                .withMessageContaining("positive");
    }

    @Test
    void should_throw_when_item_id_is_null() {
        assertThatNullPointerException()
                .isThrownBy(() -> Sale.record(null, null, 1));
    }
}
