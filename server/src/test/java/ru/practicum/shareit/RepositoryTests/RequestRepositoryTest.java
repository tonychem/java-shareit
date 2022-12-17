package ru.practicum.shareit.RepositoryTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class RequestRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private RequestRepository itemRequestRepository;

    @Test
    public void shouldReturnItemRequestsOrdered() {
        User user = new User(0, "user", "user@email.com");

        LocalDateTime theEarliest = LocalDateTime.now().minusDays(10);
        LocalDateTime earlier = LocalDateTime.now().minusDays(5);
        LocalDateTime theLatest = LocalDateTime.now();

        ItemRequest itemRequestTheEarliest = new ItemRequest(0, "item1", user, theEarliest);
        ItemRequest itemRequestEarlier = new ItemRequest(0, "item2", user, earlier);
        ItemRequest itemRequestTheLatest = new ItemRequest(0, "item3", user, theLatest);

        long savedUserId = em.persist(user).getId();
        em.persist(itemRequestTheEarliest);
        em.persist(itemRequestEarlier);
        em.persist(itemRequestTheLatest);
        em.flush();

        List<ItemRequest> itemRequestsByUserOrdered = itemRequestRepository.getItemRequestsByRequesterIdOrderByCreatedDesc(savedUserId);

        assertThat(itemRequestsByUserOrdered).hasSize(3);
        assertThat(itemRequestsByUserOrdered.get(1).getCreated()).isStrictlyBetween(theEarliest, theLatest);
    }

    @Test
    public void shouldReturnAllItemRequestsExcluding() {
        User userExclusiveWithItemRequests = new User(0, "user", "user@email.com");
        User userWithoutItemRequests = new User(0, "user1", "user1@email.com");

        LocalDateTime theEarliest = LocalDateTime.now().minusDays(10);
        LocalDateTime earlier = LocalDateTime.now().minusDays(5);
        LocalDateTime theLatest = LocalDateTime.now();

        ItemRequest itemRequestTheEarliest = new ItemRequest(0, "item1", userExclusiveWithItemRequests, theEarliest);
        ItemRequest itemRequestEarlier = new ItemRequest(0, "item2", userExclusiveWithItemRequests, earlier);
        ItemRequest itemRequestTheLatest = new ItemRequest(0, "item3", userExclusiveWithItemRequests, theLatest);

        long savedUserExclusiveId = em.persist(userExclusiveWithItemRequests).getId();
        long savedUserWithoutItemRequestsId = em.persist(userWithoutItemRequests).getId();

        long savedItemRequestTheEarliest = em.persist(itemRequestTheEarliest).getId();
        long savedItemRequestEarlier = em.persist(itemRequestEarlier).getId();
        long savedItemRequestTheLatest = em.persist(itemRequestTheLatest).getId();
        em.flush();

        Page<ItemRequest> requestsByOthers = itemRequestRepository.getItemRequestsByRequesterIdIsNotOrderByCreatedDesc(savedUserWithoutItemRequestsId, Pageable.unpaged());
        Page<ItemRequest> requestsByUserWithoutRequests = itemRequestRepository.getItemRequestsByRequesterIdIsNotOrderByCreatedDesc(savedUserExclusiveId, Pageable.unpaged());

        assertThat(requestsByOthers.getTotalElements()).isEqualTo(3);
        assertThat(requestsByUserWithoutRequests.getTotalElements()).isEqualTo(0);
    }
}
