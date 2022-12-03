package ru.practicum.shareit.RepositoryTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    public void shouldFind2CommentsByAuthorId() {
        User owner = new User(0, "owner", "mail@email.com");
        Item item = new Item(0, "item1", "desc1", true, owner, null);

        User author = new User(0, "commentMaker", "comment@email.com");
        Comment comment1 = new Comment(0, "comment1", author, item, LocalDateTime.now());
        Comment comment2 = new Comment(0, "comment2", author, item, LocalDateTime.now().minusDays(2));

        em.persist(owner);
        em.persist(author);
        long itemId = em.persist(item).getId();
        em.persist(comment1);
        em.persist(comment2);
        em.flush();

        List<Comment> comments = commentRepository.findCommentsByItemId(itemId);
        assertThat(comments).hasSize(2);
    }

    @Test
    public void shouldReturnEmptyListOnNonExistingItemId() {
        List<Comment> comments = commentRepository.findCommentsByItemId(100);
        assertThat(comments).isEmpty();
    }
}
