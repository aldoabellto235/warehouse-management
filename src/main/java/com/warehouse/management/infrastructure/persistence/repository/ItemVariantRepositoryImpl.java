package com.warehouse.management.infrastructure.persistence.repository;

import com.warehouse.management.domain.model.ItemVariant;
import com.warehouse.management.domain.repository.ItemVariantRepository;
import com.warehouse.management.infrastructure.persistence.mapper.ItemVariantJpaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ItemVariantRepositoryImpl implements ItemVariantRepository {

    private final ItemVariantJpaRepository jpaRepository;
    private final ItemVariantJpaMapper mapper;

    @Override
    public void save(ItemVariant variant) {
        jpaRepository.save(mapper.toJpa(variant));
    }

    @Override
    public Optional<ItemVariant> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<ItemVariant> findByItemId(UUID itemId) {
        return jpaRepository.findByItemId(itemId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsBySku(String sku) {
        return jpaRepository.existsBySku(sku);
    }

    @Override
    public boolean existsBySkuAndIdNot(String sku, UUID excludeId) {
        return jpaRepository.existsBySkuAndIdNot(sku, excludeId);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}
