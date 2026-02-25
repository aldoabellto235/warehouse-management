package com.warehouse.management.infrastructure.persistence.repository;

import com.warehouse.management.infrastructure.persistence.entity.ItemVariantJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ItemVariantJpaRepository extends JpaRepository<ItemVariantJpaEntity, UUID> {

    List<ItemVariantJpaEntity> findByItemId(UUID itemId);

    boolean existsBySku(String sku);

    boolean existsBySkuAndIdNot(String sku, UUID id);
}
