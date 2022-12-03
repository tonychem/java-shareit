package ru.practicum.shareit.RepositoryTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    public void shouldReturnEmptyListForNonExistingRequestId() {
        List<Item> items = itemRepository.getItemsByRequestId(100);
        assertThat(items).isEmpty();
    }

    @Test
    public void shouldReturnItemsForGivenRequestId() {
        User owner1 = new User(0, "owner1", "owner1@email.com");
        User owner2 = new User(0, "owner2", "owner2@email.com");
        User requester = new User(0, "requester", "requester@email.com");
        ItemRequest request = new ItemRequest(0, "screwdriver", requester, LocalDateTime.now());
        Item itemOwnedByOwner1RequestedByRequester = new Item(0, "screwdriver", "desc", true, owner1, request);
        Item itemOwnedByOwner2 = new Item(0, "remote controlled car", "desc", true, owner2, null);
        Item anotherItemOwnedByOwner2 = new Item(0, "tv", "desc", true, owner2, null);

        em.persist(owner1);
        em.persist(owner2);
        em.persist(requester);
        long itemRequest = em.persist(request).getId();
        em.persist(itemOwnedByOwner1RequestedByRequester);
        em.persist(itemOwnedByOwner2);
        em.persist(anotherItemOwnedByOwner2);
        em.flush();

        List<Item> items = itemRepository.getItemsByRequestId(itemRequest);
        assertThat(items).hasSize(1);
    }

    @Test
    public void shouldReturnItemsOwnedBy() {
        User owner = new User(0, "owner2", "owner2@email.com");
        Item itemOwnedByOwner = new Item(0, "remote controlled car", "desc", true, owner, null);
        Item anotherItemOwnedByOwner = new Item(0, "tv", "desc", true, owner, null);

        long ownerId = em.persist(owner).getId();
        em.persist(itemOwnedByOwner);
        em.persist(anotherItemOwnedByOwner);
        em.flush();

        List<Item> items = itemRepository.findItemsOwnedBy(ownerId);
        assertThat(items).hasSize(2);
    }

    @Test
    public void shouldReturnEmptyListOfOwnedItemsByRequester() {
        User owner1 = new User(0, "owner1", "owner1@email.com");
        User owner2 = new User(0, "owner2", "owner2@email.com");
        User requester = new User(0, "requester", "requester@email.com");
        ItemRequest request = new ItemRequest(0, "screwdriver", requester, LocalDateTime.now());
        Item itemOwnedByOwner1RequestedByRequester = new Item(0, "screwdriver", "desc", true, owner1, request);
        Item itemOwnedByOwner2 = new Item(0, "remote controlled car", "desc", true, owner2, null);
        Item anotherItemOwnedByOwner2 = new Item(0, "tv", "desc", true, owner2, null);

        em.persist(owner1);
        em.persist(owner2);
        long requesterId = em.persist(requester).getId();
        em.persist(request);
        em.persist(itemOwnedByOwner1RequestedByRequester);
        em.persist(itemOwnedByOwner2);
        em.persist(anotherItemOwnedByOwner2);
        em.flush();

        List<Item> items = itemRepository.findItemsOwnedBy(requesterId);
        assertThat(items).isEmpty();
    }

    @Test
    public void shouldFindByNameIgnoreCase() {
        User owner1 = new User(0, "owner1", "owner1@email.com");
        User owner2 = new User(0, "owner2", "owner2@email.com");
        User requester = new User(0, "requester", "requester@email.com");
        ItemRequest request = new ItemRequest(0, "screwdriver", requester, LocalDateTime.now());
        Item itemOwnedByOwner1RequestedByRequester = new Item(0, "screwdriver", "desc", true, owner1, request);
        Item itemOwnedByOwner2 = new Item(0, "remote controlled car", "desc", true, owner2, null);
        Item anotherItemOwnedByOwner2 = new Item(0, "tv", "desc", true, owner2, null);

        em.persist(owner1);
        em.persist(owner2);
        em.persist(requester).getId();
        em.persist(request);
        em.persist(itemOwnedByOwner1RequestedByRequester);
        em.persist(itemOwnedByOwner2);
        em.persist(anotherItemOwnedByOwner2);
        em.flush();

        List<Item> itemsLowerCaseSearch = itemRepository.search("screwdriver");
        List<Item> itemsAnyCaseSearch = itemRepository.search("ScreWDRiver");

        assertThat(itemsLowerCaseSearch).hasSize(1);
        assertThat(itemsAnyCaseSearch).hasSize(1);
        assertThat(itemsLowerCaseSearch).isEqualTo(itemsAnyCaseSearch);
    }

    @Test
    public void shouldFindByDescriptionIgnoreCase() {
        User owner1 = new User(0, "owner1", "owner1@email.com");
        User owner2 = new User(0, "owner2", "owner2@email.com");
        User requester = new User(0, "requester", "requester@email.com");
        ItemRequest request = new ItemRequest(0, "screwdriver", requester, LocalDateTime.now());
        Item itemOwnedByOwner1RequestedByRequester = new Item(0, "screwdriver", "desc", true, owner1, request);
        Item itemOwnedByOwner2 = new Item(0, "remote controlled car", "desc", true, owner2, null);
        Item anotherItemOwnedByOwner2 = new Item(0, "tv", "desc", true, owner2, null);

        em.persist(owner1);
        em.persist(owner2);
        em.persist(requester).getId();
        em.persist(request);
        em.persist(itemOwnedByOwner1RequestedByRequester);
        em.persist(itemOwnedByOwner2);
        em.persist(anotherItemOwnedByOwner2);
        em.flush();

        List<Item> itemsLowerCaseSearch = itemRepository.search("desc");
        List<Item> itemsAnyCaseSearch = itemRepository.search("DeSC");

        assertThat(itemsLowerCaseSearch).hasSize(3);
        assertThat(itemsAnyCaseSearch).hasSize(3);
        assertThat(itemsLowerCaseSearch).isEqualTo(itemsAnyCaseSearch);
    }
}
