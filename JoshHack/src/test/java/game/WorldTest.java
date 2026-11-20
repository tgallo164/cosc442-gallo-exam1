package game;

import java.awt.Color;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class WorldTest {
    @Test
    void boundsAndDiggingRespectWorldTiles() {
        Tile[][][] tiles = tiles(3, 3, 1, Tile.FLOOR);
        tiles[1][1][0] = Tile.WALL;
        World world = new World(tiles);

        assertEquals(Tile.BOUNDS, world.tile(-1, 0, 0));
        assertEquals(Tile.BOUNDS, world.tile(3, 0, 0));
        assertEquals(Tile.BOUNDS, world.tile(0, -1, 0));
        assertEquals(Tile.BOUNDS, world.tile(0, 3, 0));
        assertEquals(Tile.BOUNDS, world.tile(0, 0, -1));
        assertEquals(Tile.BOUNDS, world.tile(0, 0, 1));

        world.dig(1, 1, 0);
        world.dig(0, 0, 0);

        assertEquals(Tile.FLOOR, world.tile(1, 1, 0));
        assertEquals(Tile.FLOOR, world.tile(0, 0, 0));
        assertEquals(Tile.WALL, tiles[1][1][0]);
    }

    @Test
    void addAtEmptySpaceUsesAnOpenNeighborWhenOriginIsOccupied() {
        World world = new World(tiles(3, 3, 1, Tile.FLOOR));
        Item first = new Item('%', Color.GRAY, "first", null);
        Item second = new Item('%', Color.GRAY, "second", null);

        assertTrue(world.addAtEmptySpace(first, 1, 1, 0));
        assertTrue(world.addAtEmptySpace(second, 1, 1, 0));

        assertSame(first, world.item(1, 1, 0));
        assertTrue(containsItem(world, second));
    }

    @Test
    void addAtEmptySpaceReturnsFalseWhenNoReachableSpaceExists() {
        Tile[][][] tiles = tiles(3, 3, 1, Tile.WALL);
        tiles[1][1][0] = Tile.FLOOR;
        World world = new World(tiles);
        Item first = new Item('%', Color.GRAY, "first", null);
        Item second = new Item('%', Color.GRAY, "second", null);

        assertTrue(world.addAtEmptySpace(first, 1, 1, 0));
        assertFalse(world.addAtEmptySpace(second, 1, 1, 0));
        assertSame(first, world.item(1, 1, 0));
    }

    @Test
    void constructorCopiesTheTileArray() {
        Tile[][][] tiles = tiles(2, 2, 1, Tile.FLOOR);
        tiles[1][1][0] = Tile.WALL;
        World world = new World(tiles);

        tiles[0][0][0] = Tile.WALL;
        world.dig(1, 1, 0);

        assertEquals(Tile.FLOOR, world.tile(0, 0, 0));
        assertEquals(Tile.FLOOR, world.tile(1, 1, 0));
        assertEquals(Tile.WALL, tiles[1][1][0]);
    }

    private static Tile[][][] tiles(int width, int height, int depth, Tile tile) {
        Tile[][][] tiles = new Tile[width][height][depth];
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                for (int z = 0; z < depth; z++)
                    tiles[x][y][z] = tile;
        return tiles;
    }

    private static boolean containsItem(World world, Item expected) {
        for (int x = 0; x < world.width(); x++)
            for (int y = 0; y < world.height(); y++)
                for (int z = 0; z < world.depth(); z++)
                    if (world.item(x, y, z) == expected)
                        return true;
        return false;
    }
}
