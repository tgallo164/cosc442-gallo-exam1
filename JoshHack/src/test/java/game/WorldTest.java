package game;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
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

    @Test
    void reportsDimensionsAndFindsCreaturesByLocation() {
        World world = new World(tiles(2, 3, 2, Tile.FLOOR));
        Creature creature = creature(world, '@', Color.BLUE, "player", 1, 2, 1);
        world.add(creature);

        assertEquals(2, world.width());
        assertEquals(3, world.height());
        assertEquals(2, world.depth());
        assertSame(creature, world.creature(1, 2, 1));
        assertNull(world.creature(0, 0, 0));

        world.remove(creature);
        assertNull(world.creature(1, 2, 1));
    }

    @Test
    void glyphAndColorPreferCreatureThenItemThenTile() {
        World world = new World(tiles(2, 2, 1, Tile.FLOOR));
        Item item = new Item('!', Color.RED, "potion", null);
        Creature creature = creature(world, '@', Color.BLUE, "player", 0, 0, 0);

        assertTrue(world.addAtEmptySpace(item, 0, 0, 0));
        assertEquals(item.glyph(), world.glyph(0, 0, 0));
        assertSame(item.color(), world.color(0, 0, 0));

        world.add(creature);
        assertEquals(creature.glyph(), world.glyph(0, 0, 0));
        assertSame(creature.color(), world.color(0, 0, 0));

        world.remove(creature);
        assertEquals(item.glyph(), world.glyph(0, 0, 0));
        assertSame(item.color(), world.color(0, 0, 0));

        world.remove(item);
        assertEquals(Tile.FLOOR.glyph(), world.glyph(0, 0, 0));
        assertSame(Tile.FLOOR.color(), world.color(0, 0, 0));
    }

    @Test
    void addAtEmptyLocationPlacesCreatureAndItemOnOnlyAvailableTile() {
        World world = new World(tiles(1, 1, 1, Tile.FLOOR));
        Creature creature = creature(world, '@', Color.BLUE, "player", -1, -1, -1);
        Item item = new Item('!', Color.RED, "potion", null);

        world.addAtEmptyLocation(creature, 0);
        world.addAtEmptyLocation(item, 0);

        assertSame(creature, world.creature(0, 0, 0));
        assertSame(item, world.item(0, 0, 0));
        assertEquals(0, creature.x);
        assertEquals(0, creature.y);
        assertEquals(0, creature.z);
    }

    @Test
    void updateUsesSnapshotWhenCreatureRemovesItself() {
        World world = new World(tiles(3, 1, 1, Tile.FLOOR));
        Creature dying = creature(world, 'd', Color.RED, "dying", 0, 0, 0);
        Creature survivor = creature(world, 's', Color.BLUE, "survivor", 2, 0, 0);
        CountingAi dyingAi = new CountingAi(dying);
        CountingAi survivorAi = new CountingAi(survivor);
        dyingAi.onUpdateAction = () -> dying.modifyHp(-10, "test");
        world.add(dying);
        world.add(survivor);

        world.update();

        assertEquals(1, dyingAi.updates);
        assertEquals(1, survivorAi.updates);
        assertNull(world.creature(0, 0, 0));
        assertSame(survivor, world.creature(2, 0, 0));
    }

    @Test
    void addAtEmptySpaceHandlesNullAndNotifiesCreatureAtLandingLocation() {
        World world = new World(tiles(2, 2, 1, Tile.FLOOR));
        Creature creature = creature(world, '@', Color.BLUE, "player", 0, 0, 0);
        CountingAi ai = new CountingAi(creature);
        world.add(creature);
        Item item = new Item('!', Color.RED, "potion", null);

        assertTrue(world.addAtEmptySpace(null, 0, 0, 0));
        assertTrue(world.addAtEmptySpace(item, 0, 0, 0));

        assertSame(item, world.item(0, 0, 0));
        assertTrue(ai.notifications.contains("A potion lands between your feet."));
    }

    @Test
    void removeByCoordinatesClearsOnlyTheSelectedItem() {
        World world = new World(tiles(2, 2, 1, Tile.FLOOR));
        Item first = new Item('!', Color.RED, "first", null);
        Item second = new Item('?', Color.BLUE, "second", null);
        assertTrue(world.addAtEmptySpace(first, 0, 0, 0));
        assertTrue(world.addAtEmptySpace(second, 1, 1, 0));

        world.remove(1, 1, 0);

        assertSame(first, world.item(0, 0, 0));
        assertNull(world.item(1, 1, 0));
    }

    @Test
    void removeItemSearchesAllCellsAndLeavesNonmatchingItems() {
        World world = new World(tiles(2, 2, 2, Tile.FLOOR));
        Item preserved = new Item('!', Color.RED, "potion", null);
        Item target = new Item('?', Color.BLUE, "scroll", null);
        Item nonmatching = new Item('?', Color.BLUE, "scroll", null);
        assertTrue(world.addAtEmptySpace(preserved, 0, 0, 0));
        assertTrue(world.addAtEmptySpace(target, 1, 1, 1));

        world.remove(nonmatching);
        assertSame(preserved, world.item(0, 0, 0));
        assertSame(target, world.item(1, 1, 1));

        world.remove(target);
        assertSame(preserved, world.item(0, 0, 0));
        assertNull(world.item(1, 1, 1));
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

    private static Creature creature(World world, char glyph, Color color, String name,
            int x, int y, int z) {
        Creature creature = new Creature(world, glyph, color, name, 10, 1, 0);
        creature.x = x;
        creature.y = y;
        creature.z = z;
        return creature;
    }

    private static class CountingAi extends CreatureAi {
        private int updates;
        private Runnable onUpdateAction;
        private final List<String> notifications = new ArrayList<>();

        CountingAi(Creature creature) {
            super(creature);
        }

        @Override
        public void onUpdate() {
            updates++;
            if (onUpdateAction != null)
                onUpdateAction.run();
        }

        @Override
        public void onNotify(String message) {
            notifications.add(message);
        }
    }
}
