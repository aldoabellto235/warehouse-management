package com.warehouse.management.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class ItemVariantTest {

    private final UUID itemId = UUID.randomUUID();

    @Test
    void should_create_variant_when_valid_inputs() {
        ItemVariant variant = ItemVariant.create(itemId, "Red / Large", "TSHIRT-RED-L",
                new BigDecimal("29.99"), Map.of("color", "red", "size", "L"));

        assertThat(variant.getId()).isNotNull();
        assertThat(variant.getItemId()).isEqualTo(itemId);
        assertThat(variant.getName()).isEqualTo("Red / Large");
        assertThat(variant.getSku()).isEqualTo("TSHIRT-RED-L");
        assertThat(variant.getPrice()).isEqualByComparingTo("29.99");
        assertThat(variant.getAttributes()).containsEntry("color", "red");
        assertThat(variant.getCreatedAt()).isNotNull();
    }

    @Test
    void should_uppercase_sku_on_create() {
        ItemVariant variant = ItemVariant.create(itemId, "Blue", "tshirt-blue-m",
                new BigDecimal("29.99"), null);

        assertThat(variant.getSku()).isEqualTo("TSHIRT-BLUE-M");
    }

    @Test
    void should_use_empty_map_when_attributes_are_null() {
        ItemVariant variant = ItemVariant.create(itemId, "Plain", "SKU-001",
                new BigDecimal("10.00"), null);

        assertThat(variant.getAttributes()).isEmpty();
    }

    @Test
    void should_throw_when_name_is_blank() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> ItemVariant.create(itemId, "  ", "SKU-001", new BigDecimal("10.00"), null))
                .withMessageContaining("name");
    }

    @Test
    void should_throw_when_sku_is_blank() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> ItemVariant.create(itemId, "Red", "  ", new BigDecimal("10.00"), null))
                .withMessageContaining("SKU");
    }

    @Test
    void should_throw_when_price_is_negative() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> ItemVariant.create(itemId, "Red", "SKU-001", new BigDecimal("-0.01"), null))
                .withMessageContaining("Price");
    }

    @Test
    void should_allow_zero_price() {
        ItemVariant variant = ItemVariant.create(itemId, "Free", "FREE-SKU",
                BigDecimal.ZERO, null);

        assertThat(variant.getPrice()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void should_update_variant_fields() {
        ItemVariant variant = ItemVariant.create(itemId, "Red / Large", "OLD-SKU",
                new BigDecimal("29.99"), null);

        variant.update("Blue / Medium", "NEW-SKU", new BigDecimal("34.99"), Map.of("color", "blue"));

        assertThat(variant.getName()).isEqualTo("Blue / Medium");
        assertThat(variant.getSku()).isEqualTo("NEW-SKU");
        assertThat(variant.getPrice()).isEqualByComparingTo("34.99");
        assertThat(variant.getAttributes()).containsEntry("color", "blue");
    }

    @Test
    void should_uppercase_sku_on_update() {
        ItemVariant variant = ItemVariant.create(itemId, "Red", "OLD-SKU", new BigDecimal("10.00"), null);

        variant.update("Red", "new-sku-123", new BigDecimal("10.00"), null);

        assertThat(variant.getSku()).isEqualTo("NEW-SKU-123");
    }

    @Test
    void should_throw_when_updating_with_blank_name() {
        ItemVariant variant = ItemVariant.create(itemId, "Red", "SKU-001", new BigDecimal("10.00"), null);

        assertThatIllegalArgumentException()
                .isThrownBy(() -> variant.update("", "SKU-001", new BigDecimal("10.00"), null))
                .withMessageContaining("name");
    }

    @Test
    void should_throw_when_updating_with_negative_price() {
        ItemVariant variant = ItemVariant.create(itemId, "Red", "SKU-001", new BigDecimal("10.00"), null);

        assertThatIllegalArgumentException()
                .isThrownBy(() -> variant.update("Red", "SKU-001", new BigDecimal("-1.00"), null))
                .withMessageContaining("Price");
    }
}
