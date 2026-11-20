package game;

import java.awt.Color;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class PathFinderTest {
    @Test
    void findsAValidPathAroundObstacles() {
        Tile[][][] tiles = tiles(6, 6, 1, Tile.FLOOR);
        for (int y = 0; y < 5; y++)
            tiles[2][y][0] = Tile.WALL;

        World world = new World(tiles);
        Creature creature = creatureAt(world, 0, 2);
        Point start = new Point(0, 2, 0);
        Point end = new Point(4, 2, 0);

        List<Point> path = new PathFinder().findPath(creature, start, end, 100);

        assertNotNull(path);
        assertEquals(end, path.get(path.size() - 1));
        Point previous = start;
        for (Point point : path) {
            assertTrue(world.tile(point.x, point.y, point.z).isGround());
            assertTrue(Math.abs(point.x - previous.x) <= 1);
            assertTrue(Math.abs(point.y - previous.y) <= 1);
            assertTrue(point.x != previous.x || point.y != previous.y);
            previous = point;
        }
    }

    @Test
    void returnsNullWhenWallsSeparateTheTarget() {
        Tile[][][] tiles = tiles(5, 3, 1, Tile.FLOOR);
        for (int y = 0; y < 3; y++)
            tiles[2][y][0] = Tile.WALL;

        World world = new World(tiles);
        Creature creature = creatureAt(world, 0, 1);

        assertNull(new PathFinder().findPath(
                creature, new Point(0, 1, 0), new Point(4, 1, 0), 100));
    }

    @Test
    void pathAndLineAccessorsReturnDefensiveReadOnlySnapshots() {
        World world = new World(tiles(4, 4, 1, Tile.FLOOR));
        Creature creature = creatureAt(world, 0, 0);
        Path path = new Path(creature, 3, 3);

        Point pathPoint = path.points().get(0);
        int originalX = path.points().get(0).x;
        pathPoint.x = 99;
        assertEquals(originalX, path.points().get(0).x);
        assertThrows(UnsupportedOperationException.class, () -> path.points().clear());

        Line line = new Line(0, 0, 3, 3);
        Point linePoint = line.getPoints().get(0);
        linePoint.x = 99;
        assertEquals(0, line.getPoints().get(0).x);
        assertThrows(UnsupportedOperationException.class, () -> line.getPoints().clear());

        Point iteratedPoint = line.iterator().next();
        iteratedPoint.x = 99;
        assertEquals(0, line.getPoints().get(0).x);
    }

    private static Creature creatureAt(World world, int x, int y) {
        Creature creature = new Creature(world, '@', Color.GRAY, "test", 10, 1, 0);
        creature.x = x;
        creature.y = y;
        creature.z = 0;
        world.add(creature);
        return creature;
    }

    private static Tile[][][] tiles(int width, int height, int depth, Tile tile) {
        Tile[][][] tiles = new Tile[width][height][depth];
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                for (int z = 0; z < depth; z++)
                    tiles[x][y][z] = tile;
        return tiles;
    }
}
