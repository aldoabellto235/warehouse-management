CREATE TABLE stocks (
    id         UUID        NOT NULL DEFAULT gen_random_uuid(),
    item_id    UUID        NOT NULL,
    variant_id UUID,
    quantity   INT         NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT pk_stocks PRIMARY KEY (id),
    CONSTRAINT uq_stocks_item_variant UNIQUE (item_id, variant_id),
    CONSTRAINT chk_stocks_quantity CHECK (quantity >= 0),
    CONSTRAINT fk_stocks_item FOREIGN KEY (item_id)
        REFERENCES items (id) ON DELETE CASCADE,
    CONSTRAINT fk_stocks_variant FOREIGN KEY (variant_id)
        REFERENCES item_variants (id) ON DELETE CASCADE
);

CREATE INDEX idx_stocks_item_id ON stocks (item_id);
CREATE INDEX idx_stocks_variant_id ON stocks (variant_id);
