package com.warehouse.management.infrastructure.persistence.repository;

import com.warehouse.management.infrastructure.persistence.entity.ItemJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ItemJpaRepository extends JpaRepository<ItemJpaEntity, UUID> {

    @Query("""
            SELECT DISTINCT i FROM ItemJpaEntity i
            WHERE (:name IS NULL OR LOWER(i.name) LIKE LOWER(CONCAT('%', :name, '%')))
            AND (:inStockOnly = false OR EXISTS (
                SELECT s FROM StockJpaEntity s
                WHERE s.itemId = i.id AND s.quantity > 0
            ))
            """)
    Page<ItemJpaEntity> search(@Param("name") String name,
                               @Param("inStockOnly") boolean inStockOnly,
                               Pageable pageable);
}
