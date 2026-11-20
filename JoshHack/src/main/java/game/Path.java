package game;

import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

public class Path {

	private static PathFinder pf = new PathFinder();
	
	private List<Point> points;
	public List<Point> points() {
		if (points == null)
			return null;

		List<Point> snapshot = new ArrayList<Point>(points.size());
		for (Point point : points)
			snapshot.add(new Point(point.x, point.y, point.z));
		return Collections.unmodifiableList(snapshot);
	}
	
	public Path(Creature creature, int x, int y){
		points = pf.findPath(creature, new Point(creature.x, creature.y, creature.z), new Point(x, y, creature.z), 300);
	}
}
