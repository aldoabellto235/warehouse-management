package com.warehouse.management.infrastructure.persistence.repository;

import com.warehouse.management.domain.model.Sale;
import com.warehouse.management.domain.repository.SaleRepository;
import com.warehouse.management.infrastructure.persistence.mapper.SaleJpaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class SaleRepositoryImpl implements SaleRepository {

    private final SaleJpaRepository jpaRepository;
    private final SaleJpaMapper mapper;

    @Override
    public void save(Sale sale) {
        jpaRepository.save(mapper.toJpa(sale));
    }

    @Override
    public List<Sale> findByItemId(UUID itemId, int page, int size) {
        return jpaRepository.findByItemIdOrderBySoldAtDesc(itemId, PageRequest.of(page, size))
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public long countByItemId(UUID itemId) {
        return jpaRepository.countByItemId(itemId);
    }

    @Override
    public List<Sale> findByVariantId(UUID variantId, int page, int size) {
        return jpaRepository.findByVariantIdOrderBySoldAtDesc(variantId, PageRequest.of(page, size))
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public long countByVariantId(UUID variantId) {
        return jpaRepository.countByVariantId(variantId);
    }
}
