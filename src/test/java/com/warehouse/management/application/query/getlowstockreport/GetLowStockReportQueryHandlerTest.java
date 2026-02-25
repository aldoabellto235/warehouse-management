package com.warehouse.management.application.query.getlowstockreport;

import com.warehouse.management.infrastructure.persistence.dto.LowStockEntryDto;
import com.warehouse.management.infrastructure.persistence.repository.StockJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetLowStockReportQueryHandlerTest {

    @Mock private StockJpaRepository stockJpaRepository;
    @InjectMocks private GetLowStockReportQueryHandler handler;

    @Test
    void should_return_low_stock_entries_below_threshold() {
        UUID itemId = UUID.randomUUID();
        LowStockEntryDto dto = new LowStockEntryDto(itemId, "T-Shirt", null, null, null, 3);
        when(stockJpaRepository.findLowStock(5, PageRequest.of(0, 20)))
                .thenReturn(new PageImpl<>(List.of(dto)));

        LowStockReportPageResult result = handler.handle(new GetLowStockReportQuery(5, 0, 20));

        assertThat(result.entries()).hasSize(1);
        assertThat(result.entries().get(0).itemName()).isEqualTo("T-Shirt");
        assertThat(result.entries().get(0).quantity()).isEqualTo(3);
        assertThat(result.entries().get(0).threshold()).isEqualTo(5);
        assertThat(result.totalEntries()).isEqualTo(1L);
    }

    @Test
    void should_return_empty_when_all_stock_above_threshold() {
        when(stockJpaRepository.findLowStock(5, PageRequest.of(0, 20)))
                .thenReturn(new PageImpl<>(List.of()));

        LowStockReportPageResult result = handler.handle(new GetLowStockReportQuery(5, 0, 20));

        assertThat(result.entries()).isEmpty();
        assertThat(result.totalEntries()).isZero();
    }

    @Test
    void should_include_variant_details_when_present() {
        UUID itemId = UUID.randomUUID();
        UUID variantId = UUID.randomUUID();
        LowStockEntryDto dto = new LowStockEntryDto(itemId, "Hoodie", variantId, "Red / Large", "HOODIE-RED-L", 1);
        when(stockJpaRepository.findLowStock(5, PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(List.of(dto)));

        LowStockReportPageResult result = handler.handle(new GetLowStockReportQuery(5, 0, 10));

        LowStockReportResult entry = result.entries().get(0);
        assertThat(entry.variantId()).isEqualTo(variantId);
        assertThat(entry.variantName()).isEqualTo("Red / Large");
        assertThat(entry.sku()).isEqualTo("HOODIE-RED-L");
    }

    @Test
    void should_pass_correct_pagination_to_repository() {
        when(stockJpaRepository.findLowStock(10, PageRequest.of(2, 5)))
                .thenReturn(new PageImpl<>(List.of()));

        handler.handle(new GetLowStockReportQuery(10, 2, 5));

        verify(stockJpaRepository).findLowStock(10, PageRequest.of(2, 5));
    }
}
