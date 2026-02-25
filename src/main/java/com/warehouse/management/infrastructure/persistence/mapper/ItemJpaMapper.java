package com.warehouse.management.infrastructure.persistence.mapper;

import com.warehouse.management.domain.model.Item;
import com.warehouse.management.infrastructure.persistence.entity.ItemJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ItemJpaMapper {

    public ItemJpaEntity toJpa(Item item) {
        ItemJpaEntity entity = new ItemJpaEntity();
        entity.setId(item.getId());
        entity.setName(item.getName());
        entity.setDescription(item.getDescription());
        entity.setBasePrice(item.getBasePrice());
        entity.setCreatedAt(item.getCreatedAt());
        entity.setUpdatedAt(item.getUpdatedAt());
        return entity;
    }

    public Item toDomain(ItemJpaEntity entity) {
        return Item.reconstitute(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getBasePrice(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
