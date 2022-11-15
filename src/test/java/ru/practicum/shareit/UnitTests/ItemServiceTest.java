package ru.practicum.shareit.UnitTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.requests.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    private ItemService itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Captor
    private ArgumentCaptor<Item> captor;

    @BeforeEach
    public void init() {
        itemService = new ItemServiceImpl(itemRepository, userRepository, new ItemMapper(), new BookingMapper(), requestRepository,
                bookingRepository, commentRepository, new CommentMapper());
    }

    @Test
    public void shouldUpdateItem() {
        when(userRepository.existsById(anyLong())).thenAnswer(invocationOnMock -> {
            long number = invocationOnMock.getArgument(0, Long.class);
            return number > 0;
        });

        when(itemRepository.existsById(1L)).thenReturn(true);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(new Item(1, "name", "description",
                true, new User(2, "owner", "email"), null)));
        when(itemRepository.save(any())).thenReturn(new Item(1, "none", "none", true,
                new User(), null));

        ItemDto passedDto = new ItemDto(0, "updatedName", "updatedDescription", false,
                null, null);

        //обновляет не владелец вещи
        assertThrows(SecurityException.class, () -> {
            itemService.update(10, 1, passedDto);
        });

        itemService.update(2, 1, passedDto);
        verify(itemRepository).save(captor.capture());

        assertEquals(captor.getValue().getName(), passedDto.getName());
        assertEquals(captor.getValue().getDescription(), passedDto.getDescription());
        assertEquals(captor.getValue().getAvailable(), passedDto.getAvailable());
    }
}
