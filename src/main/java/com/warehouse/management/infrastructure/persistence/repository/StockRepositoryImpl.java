package com.warehouse.management.infrastructure.persistence.repository;

import com.warehouse.management.domain.model.Stock;
import com.warehouse.management.domain.repository.StockRepository;
import com.warehouse.management.infrastructure.persistence.mapper.StockJpaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class StockRepositoryImpl implements StockRepository {

    private final StockJpaRepository jpaRepository;
    private final StockJpaMapper mapper;

    @Override
    public void save(Stock stock) {
        jpaRepository.save(mapper.toJpa(stock));
    }

    @Override
    public Optional<Stock> findByItemIdAndVariantId(UUID itemId, UUID variantId) {
        return jpaRepository.findByItemIdAndVariantId(itemId, variantId).map(mapper::toDomain);
    }

    @Override
    public List<Stock> findByItemId(UUID itemId) {
        return jpaRepository.findByItemId(itemId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
