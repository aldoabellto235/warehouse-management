package com.warehouse.management.infrastructure.persistence.mapper;

import com.warehouse.management.domain.model.ItemVariant;
import com.warehouse.management.infrastructure.persistence.entity.ItemVariantJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ItemVariantJpaMapper {

    public ItemVariantJpaEntity toJpa(ItemVariant variant) {
        ItemVariantJpaEntity entity = new ItemVariantJpaEntity();
        entity.setId(variant.getId());
        entity.setItemId(variant.getItemId());
        entity.setName(variant.getName());
        entity.setSku(variant.getSku());
        entity.setPrice(variant.getPrice());
        entity.setAttributes(variant.getAttributes());
        entity.setCreatedAt(variant.getCreatedAt());
        entity.setUpdatedAt(variant.getUpdatedAt());
        return entity;
    }

    public ItemVariant toDomain(ItemVariantJpaEntity entity) {
        return ItemVariant.reconstitute(
                entity.getId(),
                entity.getItemId(),
                entity.getName(),
                entity.getSku(),
                entity.getPrice(),
                entity.getAttributes(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
