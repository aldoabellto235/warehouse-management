package com.warehouse.management.application.query.getitembyid;

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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetItemByIdQueryHandlerTest {

    @Mock private ItemRepository itemRepository;
    @InjectMocks private GetItemByIdQueryHandler handler;

    @Test
    void should_return_result_when_item_found() {
        UUID itemId = UUID.randomUUID();
        Item item = Item.create("T-Shirt", "Cotton shirt", new BigDecimal("29.99"));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        GetItemByIdResult result = handler.handle(new GetItemByIdQuery(itemId));

        assertThat(result.name()).isEqualTo("T-Shirt");
        assertThat(result.description()).isEqualTo("Cotton shirt");
        assertThat(result.basePrice()).isEqualByComparingTo("29.99");
        assertThat(result.id()).isEqualTo(item.getId());
    }

    @Test
    void should_throw_when_item_not_found() {
        UUID itemId = UUID.randomUUID();
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(new GetItemByIdQuery(itemId)))
                .isInstanceOf(ItemNotFoundException.class);
    }
}
