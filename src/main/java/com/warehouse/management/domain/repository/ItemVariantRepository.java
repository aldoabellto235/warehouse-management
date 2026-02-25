package com.warehouse.management.domain.repository;

import com.warehouse.management.domain.model.ItemVariant;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ItemVariantRepository {

    void save(ItemVariant variant);

    Optional<ItemVariant> findById(UUID id);

    List<ItemVariant> findByItemId(UUID itemId);

    boolean existsBySku(String sku);

    boolean existsBySkuAndIdNot(String sku, UUID excludeId);

    void deleteById(UUID id);
}
