package com.warehouse.management.application.command.adjuststock;

import com.warehouse.management.domain.exception.ItemNotFoundException;
import com.warehouse.management.domain.exception.VariantNotFoundException;
import com.warehouse.management.domain.model.Stock;
import com.warehouse.management.domain.repository.ItemRepository;
import com.warehouse.management.domain.repository.ItemVariantRepository;
import com.warehouse.management.domain.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdjustStockCommandHandler {

    private final ItemRepository itemRepository;
    private final ItemVariantRepository variantRepository;
    private final StockRepository stockRepository;

    @Transactional
    public void handle(AdjustStockCommand command) {
        log.debug("Handling AdjustStockCommand: itemId={}, variantId={}, delta={}",
                command.itemId(), command.variantId(), command.delta());

        if (!itemRepository.existsById(command.itemId())) {
            throw new ItemNotFoundException(command.itemId());
        }

        if (command.variantId() != null && variantRepository.findById(command.variantId()).isEmpty()) {
            throw new VariantNotFoundException(command.variantId());
        }

        Stock stock = stockRepository
                .findByItemIdAndVariantId(command.itemId(), command.variantId())
                .orElseGet(() -> Stock.create(command.itemId(), command.variantId()));

        if (command.delta() > 0) {
            stock.add(command.delta());
        } else if (command.delta() < 0) {
            stock.deduct(Math.abs(command.delta()));
        }

        stockRepository.save(stock);
        log.debug("Stock adjusted: itemId={}, variantId={}, newQuantity={}",
                command.itemId(), command.variantId(), stock.getQuantity());
    }
}
