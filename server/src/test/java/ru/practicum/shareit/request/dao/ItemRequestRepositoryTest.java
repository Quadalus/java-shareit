package ru.practicum.shareit.request.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.common.MyPageRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRequestRepositoryTest {
    @Autowired
    ItemRequestRepository itemRequestRepository;

    private final  User requester = new User(1L, "name", "e@email.com");
    private final LocalDateTime time = LocalDateTime.now();
    private long autoIncrementId = 1L;

    @AfterEach
    public void tearDown() {
        itemRequestRepository.deleteAll();
    }

    @Test
    void findAllByRequesterIdTest() {
        ItemRequest itemRequest = new ItemRequest(autoIncrementId++, "description", requester, time);
        ItemRequest savedRequest = itemRequestRepository.save(itemRequest);

        List<ItemRequest> requests = itemRequestRepository
                .findAllByRequesterId(savedRequest.getRequester().getId());

        assertEquals(requests.size(), 1);
        assertEquals(savedRequest, requests.get(0));
    }

    @Test
    void findAllByRequesterIdNotTest() {
        ItemRequest itemRequest1 = new ItemRequest(autoIncrementId++, "description2", requester, time);
        ItemRequest savedRequest = itemRequestRepository.save(itemRequest1);

        List<ItemRequest> requests = itemRequestRepository
                .findAllByRequesterIdNot(MyPageRequest.of(0, 10), 1L);

        assertEquals(requests.size(), 1);
        assertEquals(savedRequest, requests.get(0));
    }
}