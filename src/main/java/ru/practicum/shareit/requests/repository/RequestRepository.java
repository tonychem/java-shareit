package ru.practicum.shareit.requests.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.requests.model.ItemRequest;

import java.util.List;

public interface RequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> getItemRequestsByRequesterIdOrderByCreatedDesc(long requesterId);
    Page<ItemRequest> getItemRequestsByRequesterIdIsNotOrderByCreatedDesc(long requesterIdExclusive, Pageable pageable);
}