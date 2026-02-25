package com.warehouse.management.infrastructure.persistence.repository;

import com.warehouse.management.infrastructure.persistence.dto.LowStockEntryDto;
import com.warehouse.management.infrastructure.persistence.entity.StockJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StockJpaRepository extends JpaRepository<StockJpaEntity, UUID> {

    Optional<StockJpaEntity> findByItemIdAndVariantId(UUID itemId, UUID variantId);

    List<StockJpaEntity> findByItemId(UUID itemId);

    @Query("""
            SELECT new com.warehouse.management.infrastructure.persistence.dto.LowStockEntryDto(
                s.itemId,
                i.name,
                s.variantId,
                v.name,
                v.sku,
                s.quantity
            )
            FROM StockJpaEntity s
            JOIN ItemJpaEntity i ON i.id = s.itemId
            LEFT JOIN ItemVariantJpaEntity v ON v.id = s.variantId
            WHERE s.quantity <= :threshold
            ORDER BY s.quantity ASC, i.name ASC
            """)
    Page<LowStockEntryDto> findLowStock(@Param("threshold") int threshold, Pageable pageable);
}
