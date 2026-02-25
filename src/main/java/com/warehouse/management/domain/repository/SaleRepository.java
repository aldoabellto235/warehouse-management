package com.warehouse.management.domain.repository;

import com.warehouse.management.domain.model.Sale;

import java.util.List;
import java.util.UUID;

public interface SaleRepository {

    void save(Sale sale);

    List<Sale> findByItemId(UUID itemId, int page, int size);

    long countByItemId(UUID itemId);

    List<Sale> findByVariantId(UUID variantId, int page, int size);

    long countByVariantId(UUID variantId);
}
