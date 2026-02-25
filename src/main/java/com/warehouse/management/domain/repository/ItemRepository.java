package com.warehouse.management.domain.repository;

import com.warehouse.management.domain.model.Item;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ItemRepository {

    void save(Item item);

    Optional<Item> findById(UUID id);

    List<Item> findAll(int page, int size);

    List<Item> search(String name, boolean inStockOnly, int page, int size);

    long count();

    long countSearch(String name, boolean inStockOnly);

    void deleteById(UUID id);

    boolean existsById(UUID id);
}
