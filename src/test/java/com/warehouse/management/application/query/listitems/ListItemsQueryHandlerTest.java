package com.warehouse.management.application.query.listitems;

import com.warehouse.management.domain.model.Item;
import com.warehouse.management.domain.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListItemsQueryHandlerTest {

    @Mock private ItemRepository itemRepository;
    @InjectMocks private ListItemsQueryHandler handler;

    @Test
    void should_return_all_items_when_no_filter() {
        Item item = Item.create("T-Shirt", "Cotton", new BigDecimal("29.99"));
        when(itemRepository.findAll(0, 20)).thenReturn(List.of(item));
        when(itemRepository.count()).thenReturn(1L);

        ListItemsPageResult result = handler.handle(new ListItemsQuery(null, null, 0, 20));

        assertThat(result.items()).hasSize(1);
        assertThat(result.items().get(0).name()).isEqualTo("T-Shirt");
        assertThat(result.totalItems()).isEqualTo(1L);
        verify(itemRepository).findAll(0, 20);
        verify(itemRepository).count();
    }

    @Test
    void should_use_search_when_name_filter_provided() {
        Item item = Item.create("T-Shirt", null, new BigDecimal("10.00"));
        when(itemRepository.search("shirt", false, 0, 10)).thenReturn(List.of(item));
        when(itemRepository.countSearch("shirt", false)).thenReturn(1L);

        ListItemsPageResult result = handler.handle(new ListItemsQuery("shirt", null, 0, 10));

        assertThat(result.items()).hasSize(1);
        assertThat(result.totalItems()).isEqualTo(1L);
        verify(itemRepository).search("shirt", false, 0, 10);
        verify(itemRepository, never()).findAll(anyInt(), anyInt());
    }

    @Test
    void should_use_search_when_in_stock_only_is_true() {
        when(itemRepository.search(null, true, 0, 20)).thenReturn(List.of());
        when(itemRepository.countSearch(null, true)).thenReturn(0L);

        ListItemsPageResult result = handler.handle(new ListItemsQuery(null, true, 0, 20));

        assertThat(result.items()).isEmpty();
        assertThat(result.totalItems()).isZero();
        verify(itemRepository).search(null, true, 0, 20);
    }

    @Test
    void should_trim_name_filter_before_searching() {
        when(itemRepository.search("shirt", false, 0, 20)).thenReturn(List.of());
        when(itemRepository.countSearch("shirt", false)).thenReturn(0L);

        handler.handle(new ListItemsQuery("  shirt  ", null, 0, 20));

        verify(itemRepository).search("shirt", false, 0, 20);
    }

    @Test
    void should_return_empty_when_no_items() {
        when(itemRepository.findAll(0, 20)).thenReturn(List.of());
        when(itemRepository.count()).thenReturn(0L);

        ListItemsPageResult result = handler.handle(new ListItemsQuery(null, null, 0, 20));

        assertThat(result.items()).isEmpty();
        assertThat(result.totalItems()).isZero();
    }
}
