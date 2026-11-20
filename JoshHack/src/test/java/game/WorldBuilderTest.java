package game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class WorldBuilderTest {
    @Test
    void sameSeedProducesTheSameCave() {
        World first = buildCave(123456L);
        World second = buildCave(123456L);

        for (int z = 0; z < first.depth(); z++)
            for (int x = 0; x < first.width(); x++)
                for (int y = 0; y < first.height(); y++)
                    assertEquals(first.tile(x, y, z), second.tile(x, y, z));
    }

    @Test
    void generatedCaveHasAnExitAndUsableTilesOnEveryLevel() {
        World world = buildCave(123456L);
        int exitStairs = 0;

        for (int z = 0; z < world.depth(); z++) {
            int usableTiles = 0;
            for (int x = 0; x < world.width(); x++) {
                for (int y = 0; y < world.height(); y++) {
                    Tile tile = world.tile(x, y, z);
                    assertTrue(tile != null);
                    if (tile.isGround())
                        usableTiles++;
                    if (z == 0 && tile == Tile.STAIRS_UP)
                        exitStairs++;
                }
            }
            assertTrue(usableTiles > 0);
        }

        assertEquals(1, exitStairs);

        for (int z = 0; z < world.depth() - 1; z++) {
            int downStairs = 0;
            for (int x = 0; x < world.width(); x++) {
                for (int y = 0; y < world.height(); y++) {
                    if (world.tile(x, y, z) == Tile.STAIRS_DOWN) {
                        downStairs++;
                        assertEquals(Tile.STAIRS_UP, world.tile(x, y, z + 1));
                    }
                    if (world.tile(x, y, z + 1) == Tile.STAIRS_UP)
                        assertEquals(Tile.STAIRS_DOWN, world.tile(x, y, z));
                }
            }
            assertTrue(downStairs > 0);
        }
    }

    private static World buildCave(long seed) {
        return new WorldBuilder(90, 32, 5, seed)
                .makeCaves()
                .build();
    }
}
