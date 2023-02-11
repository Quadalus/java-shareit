package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("SELECT i FROM Item i " +
            "WHERE i.isAvailable = TRUE " +
            "AND (LOWER(i.description) LIKE LOWER(CONCAT('%', ?1,'%') )" +
            "OR LOWER(i.name) LIKE LOWER(CONCAT('%', ?1, '%') ) ) ")
    List<Item> findItemsByText(String text);

    List<Item> findAllByOwnerId(Long id);

    void deleteItemById(Long itemId);
}
