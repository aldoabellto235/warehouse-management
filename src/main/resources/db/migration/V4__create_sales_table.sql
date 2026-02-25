CREATE TABLE sales (
    id         UUID        NOT NULL DEFAULT gen_random_uuid(),
    item_id    UUID        NOT NULL,
    variant_id UUID,
    quantity   INT         NOT NULL,
    sold_at    TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT pk_sales PRIMARY KEY (id),
    CONSTRAINT chk_sales_quantity CHECK (quantity > 0),
    CONSTRAINT fk_sales_item FOREIGN KEY (item_id)
        REFERENCES items (id) ON DELETE CASCADE,
    CONSTRAINT fk_sales_variant FOREIGN KEY (variant_id)
        REFERENCES item_variants (id) ON DELETE SET NULL
);

CREATE INDEX idx_sales_item_id ON sales (item_id);
CREATE INDEX idx_sales_variant_id ON sales (variant_id);
CREATE INDEX idx_sales_sold_at ON sales (sold_at DESC);
