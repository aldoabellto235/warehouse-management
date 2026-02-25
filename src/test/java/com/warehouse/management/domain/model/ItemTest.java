package com.warehouse.management.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class ItemTest {

    @Test
    void should_create_item_when_valid_inputs() {
        Item item = Item.create("T-Shirt", "Cotton shirt", new BigDecimal("29.99"));

        assertThat(item.getId()).isNotNull();
        assertThat(item.getName()).isEqualTo("T-Shirt");
        assertThat(item.getDescription()).isEqualTo("Cotton shirt");
        assertThat(item.getBasePrice()).isEqualByComparingTo("29.99");
        assertThat(item.getCreatedAt()).isNotNull();
    }

    @Test
    void should_throw_when_name_is_blank() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Item.create("  ", null, new BigDecimal("10.00")))
                .withMessageContaining("name");
    }

    @Test
    void should_throw_when_base_price_is_negative() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Item.create("Shirt", null, new BigDecimal("-1.00")))
                .withMessageContaining("price");
    }

    @Test
    void should_update_item_fields() {
        Item item = Item.create("T-Shirt", "Old desc", new BigDecimal("10.00"));

        item.update("Hoodie", "New desc", new BigDecimal("59.99"));

        assertThat(item.getName()).isEqualTo("Hoodie");
        assertThat(item.getDescription()).isEqualTo("New desc");
        assertThat(item.getBasePrice()).isEqualByComparingTo("59.99");
    }
}
