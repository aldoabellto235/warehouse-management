package com.warehouse.management.domain.repository;

import com.warehouse.management.domain.model.Stock;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StockRepository {

    void save(Stock stock);

    Optional<Stock> findByItemIdAndVariantId(UUID itemId, UUID variantId);

    List<Stock> findByItemId(UUID itemId);
}
