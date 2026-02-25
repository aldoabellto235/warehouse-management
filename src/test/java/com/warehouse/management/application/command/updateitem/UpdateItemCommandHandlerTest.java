package com.warehouse.management.application.command.updateitem;

import com.warehouse.management.domain.exception.ItemNotFoundException;
import com.warehouse.management.domain.model.Item;
import com.warehouse.management.domain.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateItemCommandHandlerTest {

    @Mock private ItemRepository itemRepository;
    @InjectMocks private UpdateItemCommandHandler handler;

    @Test
    void should_update_and_save_item_when_found() {
        UUID itemId = UUID.randomUUID();
        Item item = Item.create("Old Name", "Old desc", new BigDecimal("10.00"));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        handler.handle(new UpdateItemCommand(itemId, "New Name", "New desc", new BigDecimal("99.00")));

        verify(itemRepository).save(item);
    }

    @Test
    void should_throw_when_item_not_found() {
        UUID itemId = UUID.randomUUID();
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(new UpdateItemCommand(itemId, "Name", null, new BigDecimal("10.00"))))
                .isInstanceOf(ItemNotFoundException.class);

        verify(itemRepository, never()).save(any());
    }
}
