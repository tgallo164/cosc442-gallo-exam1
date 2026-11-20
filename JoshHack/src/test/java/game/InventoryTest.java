package game;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.awt.Color;

public class InventoryTest {
    public Inventory inventory; 
    public Item sword;

    @BeforeEach 
    public void setUp(){
    inventory = new Inventory(1);
    sword = new Item('?', Color.GRAY, "Sword", "Sharp");
    }

    @AfterEach 
    public void tearDown(){
        inventory = null;
        sword = null;
    }

    @Test
    void testAddValidBehavior() {
        assertTrue(inventory.add(sword));
        assertEquals(sword, inventory.get(0));
    }

    @Test
    void testGetRejectsInvalidIndex() {
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            inventory.get(-1);
        });
    }

    @Test
    void testIsFullBoundaryBehavior() {
        assertFalse(inventory.isFull());
        inventory.add(sword);
        assertTrue(inventory.isFull());
    }

    @Test
    void testAddRejectsNullAndFullInventory() {
        assertFalse(inventory.add(null));
        assertTrue(inventory.add(sword));

        Item shield = new Item(']', Color.GRAY, "Shield", "Round");
        assertFalse(inventory.add(shield));
        assertSame(sword, inventory.get(0));
    }

    @Test
    void testGetItemsReturnsCopyOfStorage() {
        inventory.add(sword);

        Item[] items = inventory.getItems();
        items[0] = null;

        assertSame(sword, inventory.get(0));
    }
}
