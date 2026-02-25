package com.warehouse.management.application.query.listsalesbyitem;

import com.warehouse.management.domain.exception.ItemNotFoundException;
import com.warehouse.management.domain.exception.VariantNotFoundException;
import com.warehouse.management.domain.model.Sale;
import com.warehouse.management.domain.repository.ItemRepository;
import com.warehouse.management.domain.repository.ItemVariantRepository;
import com.warehouse.management.domain.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ListSalesByItemQueryHandler {

    private final ItemRepository itemRepository;
    private final ItemVariantRepository variantRepository;
    private final SaleRepository saleRepository;

    @Transactional(readOnly = true)
    public ListSalesByItemPageResult handle(ListSalesByItemQuery query) {
        log.debug("Handling ListSalesByItemQuery: itemId={}, variantId={}", query.itemId(), query.variantId());

        if (!itemRepository.existsById(query.itemId())) {
            throw new ItemNotFoundException(query.itemId());
        }

        if (query.variantId() != null && variantRepository.findById(query.variantId()).isEmpty()) {
            throw new VariantNotFoundException(query.variantId());
        }

        List<Sale> sales;
        long total;

        if (query.variantId() != null) {
            sales = saleRepository.findByVariantId(query.variantId(), query.page(), query.size());
            total = saleRepository.countByVariantId(query.variantId());
        } else {
            sales = saleRepository.findByItemId(query.itemId(), query.page(), query.size());
            total = saleRepository.countByItemId(query.itemId());
        }

        List<ListSalesByItemResult> results = sales.stream()
                .map(s -> new ListSalesByItemResult(
                        s.getId(), s.getItemId(), s.getVariantId(), s.getQuantity(), s.getSoldAt()))
                .toList();

        return new ListSalesByItemPageResult(results, total);
    }
}
