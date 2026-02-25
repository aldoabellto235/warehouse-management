package com.warehouse.management.application.command.createitem;

import com.warehouse.management.domain.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CreateItemCommandHandlerTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private CreateItemCommandHandler handler;

    @Test
    void should_save_item_and_return_id_when_command_is_valid() {
        CreateItemCommand command = new CreateItemCommand("T-Shirt", "A cotton shirt", new BigDecimal("25.00"));

        UUID id = handler.handle(command);

        assertThat(id).isNotNull();
        ArgumentCaptor<?> captor = ArgumentCaptor.forClass(Object.class);
        verify(itemRepository).save(org.mockito.ArgumentMatchers.any());
    }
}
