package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/debug")
public class Debug {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @GetMapping(value = "/user/{userId}")
    public String getUser(@PathVariable long userId) {
        User u = userRepository.findById(userId).get();
        return u.toString();
    }

    @GetMapping(value = "/item/{itemId}")
    public String getItem(@PathVariable long itemId) {
        Item item = itemRepository.findById(itemId).get();
        return item.toString();
    }

}
