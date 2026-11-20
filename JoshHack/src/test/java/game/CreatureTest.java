package game;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CreatureTest {
    private World world;
    private Creature creature;
    private RecordingCreatureAi creatureAi;

    @BeforeEach 
    void setUp(){
        Tile[][][] tiles = new Tile[3][3][1];

        for(int x = 0; x < 3; x++){
            for(int y = 0; y < 3; y++){
                tiles[x][y][0] = Tile.FLOOR;
            }
        }

        world = new World(tiles);

        creature = new Creature(world, '@', Color.GRAY, "josh", 100, 10, 5);

        creature.x = 1;
        creature.y = 1;
        creature.z = 0;
        creatureAi = new RecordingCreatureAi(creature);
        world.add(creature);
    }

    @Test
    void testModifyHp() {
        //lowering hp but not to below 1
        creature.modifyHp(-10, "Took damage");

        //checks if hp and cause of death were updated
        assertEquals(90, creature.hp());
        assertEquals("Took damage", creature.causeOfDeath());

        //checking that the creature still exists
        assertNotNull(world.creature(1, 1, 0));
        
        //adding more hp
        creature.modifyHp(10, "Healed");

        //checks if hp and cause of death were updated
        assertEquals(100, creature.hp());
        assertEquals("Healed", creature.causeOfDeath());

        //lowering hp to 0 to cause death
        creature.modifyHp(-100, "Fell to their death");

        //checks if hp and cause of death were updated
        assertEquals(0, creature.hp());
        assertEquals("Fell to their death", creature.causeOfDeath());

        //checks to make sure the creature is deleted from the world
        assertNull(world.creature(1, 1, 0));
    }

    @Test
    void deathLeavesCorpseAndDropsInventoryItems() {
        Item sword = new Item('/', Color.GRAY, "sword", null);
        assertTrue(creature.inventory().add(sword));

        creature.modifyHp(-100, "Fell to their death");

        Item corpse = world.item(1, 1, 0);
        assertNotNull(corpse);
        assertEquals("josh corpse", corpse.name());
        assertEquals(500, corpse.foodValue());
        assertFalse(creature.inventory().contains(sword));
        assertTrue(worldContains(sword));
        assertTrue(creatureAi.notifications.contains("You die."));
    }

    @Test
    void effectsAccessorDoesNotAllowChangingCreatureState() {
        assertThrows(UnsupportedOperationException.class,
                () -> creature.effects().add(new Effect(1)));
    }

    @Test
    void pickupKeepsWorldItemWhenInventoryIsFull() {
        for (int i = 0; i < 20; i++)
            assertTrue(creature.inventory().add(new Item('?', Color.GRAY, "item", null)));

        Item groundItem = new Item('/', Color.GRAY, "sword", null);
        assertTrue(world.addAtEmptySpace(groundItem, creature.x, creature.y, creature.z));

        creature.pickup();

        assertSame(groundItem, world.item(creature.x, creature.y, creature.z));
        assertFalse(creature.inventory().contains(groundItem));
    }

    @Test
    void quaffedEffectsStartUpdateAndExpire() {
        TrackingEffect effect = new TrackingEffect();
        Item potion = new Item('!', Color.GRAY, "potion", null);
        potion.setQuaffEffect(effect);
        assertTrue(creature.inventory().add(potion));

        creature.quaff(potion);

        assertEquals(1, effect.starts);
        assertTrue(creature.effects().contains(effect));
        assertFalse(creature.inventory().contains(potion));

        creature.update();

        assertEquals(1, effect.updates);
        assertEquals(1, effect.ends);
        assertTrue(creature.effects().isEmpty());
    }

    private boolean worldContains(Item item) {
        for (int x = 0; x < world.width(); x++) {
            for (int y = 0; y < world.height(); y++) {
                if (world.item(x, y, 0) == item)
                    return true;
            }
        }
        return false;
    }

    private static class RecordingCreatureAi extends CreatureAi {
        private final List<String> notifications = new ArrayList<>();

        RecordingCreatureAi(Creature creature) {
            super(creature);
        }

        @Override
        public void onNotify(String message) {
            notifications.add(message);
        }
    }

    private static class TrackingEffect extends Effect {
        private int starts;
        private int updates;
        private int ends;

        TrackingEffect() {
            super(1);
        }

        @Override
        public void start(Creature creature) {
            starts++;
        }

        @Override
        public void update(Creature creature) {
            updates++;
            super.update(creature);
        }

        @Override
        public void end(Creature creature) {
            ends++;
        }
    }
}
