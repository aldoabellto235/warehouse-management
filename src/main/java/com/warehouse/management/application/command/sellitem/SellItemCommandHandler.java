package com.warehouse.management.application.command.sellitem;

import com.warehouse.management.domain.exception.InsufficientStockException;
import com.warehouse.management.domain.exception.ItemNotFoundException;
import com.warehouse.management.domain.exception.VariantNotFoundException;
import com.warehouse.management.domain.model.Sale;
import com.warehouse.management.domain.model.Stock;
import com.warehouse.management.domain.repository.ItemRepository;
import com.warehouse.management.domain.repository.ItemVariantRepository;
import com.warehouse.management.domain.repository.SaleRepository;
import com.warehouse.management.domain.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SellItemCommandHandler {

    private final ItemRepository itemRepository;
    private final ItemVariantRepository variantRepository;
    private final StockRepository stockRepository;
    private final SaleRepository saleRepository;

    @Transactional
    public void handle(SellItemCommand command) {
        log.debug("Handling SellItemCommand: itemId={}, variantId={}, quantity={}",
                command.itemId(), command.variantId(), command.quantity());

        if (command.quantity() <= 0) {
            throw new IllegalArgumentException("Quantity to sell must be positive");
        }

        if (!itemRepository.existsById(command.itemId())) {
            throw new ItemNotFoundException(command.itemId());
        }

        if (command.variantId() != null && variantRepository.findById(command.variantId()).isEmpty()) {
            throw new VariantNotFoundException(command.variantId());
        }

        Stock stock = stockRepository
                .findByItemIdAndVariantId(command.itemId(), command.variantId())
                .orElseThrow(() -> new InsufficientStockException(
                        command.itemId(), command.variantId(), command.quantity(), 0));

        // Domain enforces the out-of-stock guard inside Stock.deduct()
        stock.deduct(command.quantity());
        stockRepository.save(stock);

        // Record sale transaction
        Sale sale = Sale.record(command.itemId(), command.variantId(), command.quantity());
        saleRepository.save(sale);

        log.debug("Sold {} unit(s): itemId={}, variantId={}, remainingStock={}",
                command.quantity(), command.itemId(), command.variantId(), stock.getQuantity());
    }
}
