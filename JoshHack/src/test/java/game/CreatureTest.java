package game;

import java.awt.Color;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CreatureTest {
    private World world;
    private Creature creature;

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
    }

    @Test
    void testModifyHp() {
        //lowering hp but not to below 1
        creature.modifyHp(-10, "Took damage");

        //checks if hp and cause of death were updated
        assertEquals(90, creature.hp());
        assertEquals("Took damage", creature.causeOfDeath());
        
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
}
