package com.warehouse.management.application.command.createitem;

import java.math.BigDecimal;

public record CreateItemCommand(
        String name,
        String description,
        BigDecimal basePrice
) {}
