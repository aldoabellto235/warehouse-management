package com.warehouse.management.application.command.deleteitem;

import com.warehouse.management.domain.exception.ItemNotFoundException;
import com.warehouse.management.domain.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteItemCommandHandlerTest {

    @Mock private ItemRepository itemRepository;
    @InjectMocks private DeleteItemCommandHandler handler;

    @Test
    void should_delete_item_when_exists() {
        UUID itemId = UUID.randomUUID();
        when(itemRepository.existsById(itemId)).thenReturn(true);

        handler.handle(new DeleteItemCommand(itemId));

        verify(itemRepository).deleteById(itemId);
    }

    @Test
    void should_throw_when_item_not_found() {
        UUID itemId = UUID.randomUUID();
        when(itemRepository.existsById(itemId)).thenReturn(false);

        assertThatThrownBy(() -> handler.handle(new DeleteItemCommand(itemId)))
                .isInstanceOf(ItemNotFoundException.class);

        verify(itemRepository, never()).deleteById(any());
    }
}
