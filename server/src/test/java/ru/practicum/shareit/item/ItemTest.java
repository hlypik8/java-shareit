package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.Request;

import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    @Test
    void lombokNoArgsConstructor() {
        Item item = new Item(); // no-args constructor (Lombok)
        item.setId(10);
        item.setName("Шуруповёрт");
        item.setDescription("Электрический");
        item.setAvailable(true);
        item.setOwner(5);
        item.setRequest(null);

        assertEquals(10, item.getId());
        assertEquals("Шуруповёрт", item.getName());
        assertEquals("Электрический", item.getDescription());
        assertTrue(item.getAvailable());
        assertEquals(5, item.getOwner());
        assertNull(item.getRequest());
    }

    @Test
    void lombokAllArgsConstructor_setsAllFields() {
        Request req = null;
        Item item = new Item(42, "Дрель", "Аккумуляторная", false, 7, req);

        assertEquals(42, item.getId());
        assertEquals("Дрель", item.getName());
        assertEquals("Аккумуляторная", item.getDescription());
        assertFalse(item.getAvailable());
        assertEquals(7, item.getOwner());
        assertNull(item.getRequest());
    }

}