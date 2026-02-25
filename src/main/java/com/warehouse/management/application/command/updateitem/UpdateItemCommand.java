package com.warehouse.management.application.command.updateitem;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateItemCommand(
        UUID itemId,
        String name,
        String description,
        BigDecimal basePrice
) {}
