package com.warehouse.management.application.query.getlowstockreport;

import com.warehouse.management.infrastructure.persistence.dto.LowStockEntryDto;
import com.warehouse.management.infrastructure.persistence.repository.StockJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetLowStockReportQueryHandler {

    private final StockJpaRepository stockJpaRepository;

    @Transactional(readOnly = true)
    public LowStockReportPageResult handle(GetLowStockReportQuery query) {
        log.debug("Handling GetLowStockReportQuery: threshold={}, page={}, size={}",
                query.threshold(), query.page(), query.size());

        Page<LowStockEntryDto> page = stockJpaRepository.findLowStock(
                query.threshold(), PageRequest.of(query.page(), query.size()));

        List<LowStockReportResult> entries = page.getContent().stream()
                .map(dto -> new LowStockReportResult(
                        dto.itemId(), dto.itemName(),
                        dto.variantId(), dto.variantName(), dto.sku(),
                        dto.quantity(), query.threshold()))
                .toList();

        return new LowStockReportPageResult(entries, page.getTotalElements());
    }
}
