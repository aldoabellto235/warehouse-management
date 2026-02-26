package com.warehouse.management.infrastructure.config;

import com.warehouse.management.application.command.adjuststock.AdjustStockCommand;
import com.warehouse.management.application.command.adjuststock.AdjustStockCommandHandler;
import com.warehouse.management.application.command.createitem.CreateItemCommand;
import com.warehouse.management.application.command.createitem.CreateItemCommandHandler;
import com.warehouse.management.application.command.createvariant.CreateVariantCommand;
import com.warehouse.management.application.command.createvariant.CreateVariantCommandHandler;
import com.warehouse.management.domain.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

    private final ItemRepository itemRepository;
    private final CreateItemCommandHandler createItemHandler;
    private final CreateVariantCommandHandler createVariantHandler;
    private final AdjustStockCommandHandler adjustStockHandler;

    @Override
    public void run(ApplicationArguments args) {
        if (itemRepository.count() > 0) {
            log.info("DataSeeder: data already present, skipping.");
            return;
        }

        log.info("DataSeeder: seeding sample data...");

        seedTShirt();
        seedHoodie();
        seedMug();

        log.info("DataSeeder: done.");
    }

    private void seedTShirt() {
        UUID itemId = createItemHandler.handle(new CreateItemCommand(
                "T-Shirt", "100% cotton crew neck, available in multiple colors and sizes", new BigDecimal("25.00")));

        String[] colors = {"Red", "Blue", "Black"};
        String[] sizes  = {"S", "M", "L", "XL"};

        for (String color : colors) {
            for (String size : sizes) {
                String sku = "TSHIRT-" + color.toUpperCase() + "-" + size;
                UUID variantId = createVariantHandler.handle(new CreateVariantCommand(
                        itemId,
                        color + " / " + size,
                        sku,
                        new BigDecimal("25.00"),
                        Map.of("color", color.toLowerCase(), "size", size)));

                int qty = color.equals("Red") && size.equals("S") ? 2 : 50; // Red/S intentionally low
                adjustStockHandler.handle(new AdjustStockCommand(itemId, variantId, qty));
            }
        }
    }

    private void seedHoodie() {
        UUID itemId = createItemHandler.handle(new CreateItemCommand(
                "Hoodie", "Warm fleece pullover hoodie", new BigDecimal("59.99")));

        record SizePrice(String size, BigDecimal price) {}
        var variants = new SizePrice[]{
                new SizePrice("S",  new BigDecimal("59.99")),
                new SizePrice("M",  new BigDecimal("59.99")),
                new SizePrice("L",  new BigDecimal("64.99")),
                new SizePrice("XL", new BigDecimal("64.99")),
        };

        for (var v : variants) {
            UUID variantId = createVariantHandler.handle(new CreateVariantCommand(
                    itemId,
                    "Black / " + v.size(),
                    "HOODIE-BLACK-" + v.size(),
                    v.price(),
                    Map.of("color", "black", "size", v.size())));

            int qty = v.size().equals("XL") ? 0 : 20; // XL intentionally out of stock
            if (qty > 0) {
                adjustStockHandler.handle(new AdjustStockCommand(itemId, variantId, qty));
            }
        }
    }

    private void seedMug() {
        UUID itemId = createItemHandler.handle(new CreateItemCommand(
                "Coffee Mug", "Ceramic 350ml mug — no variants, tracked at item level", new BigDecimal("12.99")));

        // No variants — stock tracked at item level (variantId = null)
        adjustStockHandler.handle(new AdjustStockCommand(itemId, null, 200));
    }
}
