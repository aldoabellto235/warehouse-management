package com.warehouse.management.infrastructure.persistence.repository;

import com.warehouse.management.infrastructure.persistence.entity.SaleJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SaleJpaRepository extends JpaRepository<SaleJpaEntity, UUID> {

    Page<SaleJpaEntity> findByItemIdOrderBySoldAtDesc(UUID itemId, Pageable pageable);

    long countByItemId(UUID itemId);

    Page<SaleJpaEntity> findByVariantIdOrderBySoldAtDesc(UUID variantId, Pageable pageable);

    long countByVariantId(UUID variantId);
}
