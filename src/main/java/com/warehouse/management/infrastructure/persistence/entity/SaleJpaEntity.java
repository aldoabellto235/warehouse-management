package com.warehouse.management.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "sales")
@Getter
@Setter
@NoArgsConstructor
public class SaleJpaEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "item_id", nullable = false)
    private UUID itemId;

    @Column(name = "variant_id")
    private UUID variantId;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "sold_at", nullable = false, updatable = false)
    private Instant soldAt;
}
