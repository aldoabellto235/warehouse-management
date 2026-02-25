package com.warehouse.management.application.query.getitemsummary;

import com.warehouse.management.domain.exception.ItemNotFoundException;
import com.warehouse.management.domain.model.Item;
import com.warehouse.management.domain.model.ItemVariant;
import com.warehouse.management.domain.model.Stock;
import com.warehouse.management.domain.repository.ItemRepository;
import com.warehouse.management.domain.repository.ItemVariantRepository;
import com.warehouse.management.domain.repository.StockRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetItemSummaryQueryHandlerTest {

    @Mock private ItemRepository itemRepository;
    @Mock private ItemVariantRepository variantRepository;
    @Mock private StockRepository stockRepository;
    @InjectMocks private GetItemSummaryQueryHandler handler;

    @Test
    void should_throw_when_item_not_found() {
        UUID itemId = UUID.randomUUID();
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(new GetItemSummaryQuery(itemId)))
                .isInstanceOf(ItemNotFoundException.class);
    }

    @Test
    void should_return_summary_with_item_level_stock_when_no_variants() {
        UUID itemId = UUID.randomUUID();
        Item item = Item.create("T-Shirt", "Cotton", new BigDecimal("29.99"));
        Stock stock = Stock.create(itemId, null);
        stock.add(50);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(variantRepository.findByItemId(itemId)).thenReturn(List.of());
        when(stockRepository.findByItemIdAndVariantId(item.getId(), null)).thenReturn(Optional.of(stock));

        GetItemSummaryResult result = handler.handle(new GetItemSummaryQuery(itemId));

        assertThat(result.hasVariants()).isFalse();
        assertThat(result.totalStock()).isEqualTo(50);
        assertThat(result.variants()).isEmpty();
    }

    @Test
    void should_return_zero_stock_when_no_stock_record_and_no_variants() {
        UUID itemId = UUID.randomUUID();
        Item item = Item.create("T-Shirt", null, new BigDecimal("10.00"));

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(variantRepository.findByItemId(itemId)).thenReturn(List.of());
        when(stockRepository.findByItemIdAndVariantId(item.getId(), null)).thenReturn(Optional.empty());

        GetItemSummaryResult result = handler.handle(new GetItemSummaryQuery(itemId));

        assertThat(result.totalStock()).isZero();
        assertThat(result.hasVariants()).isFalse();
    }

    @Test
    void should_return_summary_with_variant_stocks_when_variants_exist() {
        UUID itemId = UUID.randomUUID();
        Item item = Item.create("T-Shirt", null, new BigDecimal("29.99"));
        ItemVariant variantRed = ItemVariant.create(itemId, "Red", "SKU-RED", new BigDecimal("29.99"), null);
        ItemVariant variantBlue = ItemVariant.create(itemId, "Blue", "SKU-BLUE", new BigDecimal("29.99"), null);

        Stock stockRed = Stock.create(itemId, variantRed.getId());
        stockRed.add(10);
        Stock stockBlue = Stock.create(itemId, variantBlue.getId());
        stockBlue.add(5);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(variantRepository.findByItemId(itemId)).thenReturn(List.of(variantRed, variantBlue));
        when(stockRepository.findByItemIdAndVariantId(item.getId(), variantRed.getId()))
                .thenReturn(Optional.of(stockRed));
        when(stockRepository.findByItemIdAndVariantId(item.getId(), variantBlue.getId()))
                .thenReturn(Optional.of(stockBlue));

        GetItemSummaryResult result = handler.handle(new GetItemSummaryQuery(itemId));

        assertThat(result.hasVariants()).isTrue();
        assertThat(result.totalStock()).isEqualTo(15);
        assertThat(result.variants()).hasSize(2);
        assertThat(result.variants()).anySatisfy(v -> {
            assertThat(v.name()).isEqualTo("Red");
            assertThat(v.stock()).isEqualTo(10);
            assertThat(v.available()).isTrue();
        });
    }

    @Test
    void should_show_variant_as_unavailable_when_stock_is_zero() {
        UUID itemId = UUID.randomUUID();
        Item item = Item.create("Jacket", null, new BigDecimal("99.00"));
        ItemVariant variant = ItemVariant.create(itemId, "Black", "JACKET-BLACK", new BigDecimal("99.00"), null);
        Stock stock = Stock.create(itemId, variant.getId()); // quantity = 0

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(variantRepository.findByItemId(itemId)).thenReturn(List.of(variant));
        when(stockRepository.findByItemIdAndVariantId(item.getId(), variant.getId()))
                .thenReturn(Optional.of(stock));

        GetItemSummaryResult result = handler.handle(new GetItemSummaryQuery(itemId));

        assertThat(result.variants().get(0).available()).isFalse();
        assertThat(result.totalStock()).isZero();
    }
}
