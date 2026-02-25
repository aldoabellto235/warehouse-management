package com.warehouse.management.infrastructure.persistence.mapper;

import com.warehouse.management.domain.model.Stock;
import com.warehouse.management.infrastructure.persistence.entity.StockJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class StockJpaMapper {

    public StockJpaEntity toJpa(Stock stock) {
        StockJpaEntity entity = new StockJpaEntity();
        entity.setId(stock.getId());
        entity.setItemId(stock.getItemId());
        entity.setVariantId(stock.getVariantId());
        entity.setQuantity(stock.getQuantity());
        entity.setCreatedAt(stock.getCreatedAt());
        entity.setUpdatedAt(stock.getUpdatedAt());
        return entity;
    }

    public Stock toDomain(StockJpaEntity entity) {
        return Stock.reconstitute(
                entity.getId(),
                entity.getItemId(),
                entity.getVariantId(),
                entity.getQuantity(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
