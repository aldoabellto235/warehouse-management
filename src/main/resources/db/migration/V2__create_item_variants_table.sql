CREATE TABLE item_variants (
    id         UUID           NOT NULL DEFAULT gen_random_uuid(),
    item_id    UUID           NOT NULL,
    name       VARCHAR(255)   NOT NULL,
    sku        VARCHAR(100)   NOT NULL,
    price      NUMERIC(19, 2) NOT NULL,
    attributes JSONB          NOT NULL DEFAULT '{}',
    created_at TIMESTAMPTZ    NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ    NOT NULL DEFAULT now(),

    CONSTRAINT pk_item_variants PRIMARY KEY (id),
    CONSTRAINT uq_item_variants_sku UNIQUE (sku),
    CONSTRAINT fk_item_variants_item FOREIGN KEY (item_id)
        REFERENCES items (id) ON DELETE CASCADE
);

CREATE INDEX idx_item_variants_item_id ON item_variants (item_id);
CREATE INDEX idx_item_variants_sku ON item_variants (sku);
