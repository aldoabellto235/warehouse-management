package com.warehouse.management.application.command.deleteitem;

import java.util.UUID;

public record DeleteItemCommand(UUID itemId) {}
