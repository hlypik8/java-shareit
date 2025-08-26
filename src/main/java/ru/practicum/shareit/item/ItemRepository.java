package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface ItemRepository extends JpaRepository<Item, Integer> {

    List<Item> findItemsByOwner(Integer ownerId);

    @Query("SELECT i FROM Item i " +
            "WHERE i.available = true " +
            "AND (:text IS NULL OR :text = '' OR " +
            "(i.name ILIKE %:text% OR i.description ILIKE %:text%))")
    List<Item> searchItems(String text);

    boolean existsById(Integer id);
}
