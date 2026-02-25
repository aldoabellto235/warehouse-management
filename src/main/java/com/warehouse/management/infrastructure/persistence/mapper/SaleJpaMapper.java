package com.warehouse.management.infrastructure.persistence.mapper;

import com.warehouse.management.domain.model.Sale;
import com.warehouse.management.infrastructure.persistence.entity.SaleJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class SaleJpaMapper {

    public SaleJpaEntity toJpa(Sale sale) {
        SaleJpaEntity entity = new SaleJpaEntity();
        entity.setId(sale.getId());
        entity.setItemId(sale.getItemId());
        entity.setVariantId(sale.getVariantId());
        entity.setQuantity(sale.getQuantity());
        entity.setSoldAt(sale.getSoldAt());
        return entity;
    }

    public Sale toDomain(SaleJpaEntity entity) {
        return Sale.reconstitute(
                entity.getId(),
                entity.getItemId(),
                entity.getVariantId(),
                entity.getQuantity(),
                entity.getSoldAt()
        );
    }
}
