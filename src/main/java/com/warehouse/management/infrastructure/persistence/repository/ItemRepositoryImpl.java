package com.warehouse.management.infrastructure.persistence.repository;

import com.warehouse.management.domain.model.Item;
import com.warehouse.management.domain.repository.ItemRepository;
import com.warehouse.management.infrastructure.persistence.mapper.ItemJpaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {

    private final ItemJpaRepository jpaRepository;
    private final ItemJpaMapper mapper;

    @Override
    public void save(Item item) {
        jpaRepository.save(mapper.toJpa(item));
    }

    @Override
    public Optional<Item> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Item> findAll(int page, int size) {
        return jpaRepository.findAll(PageRequest.of(page, size))
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Item> search(String name, boolean inStockOnly, int page, int size) {
        Page<Item> result = jpaRepository
                .search(name, inStockOnly, PageRequest.of(page, size))
                .map(mapper::toDomain);
        return result.getContent();
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public long countSearch(String name, boolean inStockOnly) {
        return jpaRepository.search(name, inStockOnly, PageRequest.of(0, Integer.MAX_VALUE)).getTotalElements();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }
}
