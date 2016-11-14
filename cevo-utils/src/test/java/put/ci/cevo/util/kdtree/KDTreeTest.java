package put.ci.cevo.util.kdtree;

import org.junit.Test;
import put.ci.cevo.ml.clustering.clusterable.ClusterablePoint;
import put.ci.cevo.ml.neighbors.NearestNeighbors;
import put.ci.cevo.util.Pair;
import put.ci.cevo.util.math.EuclideanDistance;

import java.util.HashSet;
import java.util.List;

import static com.google.common.collect.ImmutableList.of;
import static com.google.common.collect.Iterables.concat;
import static org.apache.commons.math3.util.FastMath.sqrt;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class KDTreeTest {

	private static final List<ClusterablePoint> aPoints = of(new ClusterablePoint(2.5, 2), new ClusterablePoint(2.5, 3), new ClusterablePoint(4, 4),
			new ClusterablePoint(3, 5), new ClusterablePoint(5, 4));
	private static final List<ClusterablePoint> bPoints = of(new ClusterablePoint(4.5, 8.5), new ClusterablePoint(6, 8), new ClusterablePoint(7, 9),
			new ClusterablePoint(7, 7), new ClusterablePoint(9, 9));
	private static final List<ClusterablePoint> cPoints = of(new ClusterablePoint(5.8, 3), new ClusterablePoint(9, 5), new ClusterablePoint(7, 5.5),
			new ClusterablePoint(6, 5));

	@Test
	public void testNearestNeighbors() {
		NearestNeighbors<ClusterablePoint> tree = new NearestNeighbors<>(setupTree(), new EuclideanDistance());

		// centers
		ClusterablePoint a = new ClusterablePoint(3, 3);
		ClusterablePoint b = new ClusterablePoint(5, 9);
		ClusterablePoint c = new ClusterablePoint(8, 4);

		List<ClusterablePoint> nearest = tree.nearest(a, 3).toList();

		assertEquals(3, nearest.size());
		assertTrue(nearest.contains(aPoints.get(0)));
		assertTrue(nearest.contains(aPoints.get(1)));
		assertTrue(nearest.contains(aPoints.get(2)));

		nearest = tree.nearest(a, 5).toList();
		assertEquals(5, nearest.size());
		assertEquals(new HashSet<>(aPoints), new HashSet<>(nearest));

		nearest = tree.nearest(b, 5).toList();
		assertEquals(5, nearest.size());
		assertEquals(new HashSet<>(bPoints), new HashSet<>(nearest));

		nearest = tree.nearest(c, 4).toList();
		assertEquals(4, nearest.size());
		assertEquals(new HashSet<>(cPoints), new HashSet<>(nearest));

	}

	@Test
	public void testNearestNeighborsDistance() {
		NearestNeighbors<ClusterablePoint> tree = new NearestNeighbors<>(setupTree(), new EuclideanDistance());

		// centers
		ClusterablePoint a = new ClusterablePoint(3, 3);
		ClusterablePoint b = new ClusterablePoint(5, 9);
		ClusterablePoint c = new ClusterablePoint(8, 4);

		Pair<ClusterablePoint, Double> max = tree.nearestWithDistance(a, 1).getFirst();
		assertEquals(aPoints.get(1), max.first());
		assertEquals(0.5, max.second(), 0.00001);

		max = tree.nearestWithDistance(b, 1).getFirst();
		assertEquals(bPoints.get(0), max.first());
		assertEquals(sqrt(2) / 2, max.second(), 0.00001);

		max = tree.nearestWithDistance(c, 1).getFirst();
		assertEquals(cPoints.get(1), max.first());
		assertEquals(sqrt(2), max.second(), 0.00001);

		List<Pair<ClusterablePoint, Double>> points = tree.nearestWithDistance(a, sqrt(3)).toList();
		assertEquals(3, points.size());
		assertEquals(aPoints.get(2), points.get(0).first());
		assertEquals(aPoints.get(0), points.get(1).first());
		assertEquals(aPoints.get(1), points.get(2).first());


		List<ClusterablePoint> n = tree.nearest(a, 3).toList();
		assertEquals(3, n.size());
		assertEquals(aPoints.get(2), n.get(0));
		assertEquals(aPoints.get(0), n.get(1));
		assertEquals(aPoints.get(1), n.get(2));

		n = tree.nearest(a, sqrt(3)).toList();
		assertEquals(3, n.size());
		assertEquals(aPoints.get(2), n.get(0));
		assertEquals(aPoints.get(0), n.get(1));
		assertEquals(aPoints.get(1), n.get(2));
	}


	private static KDTree<ClusterablePoint> setupTree() {
		KDTree<ClusterablePoint> tree = new KDTree<>(2);
		for (ClusterablePoint aPoint : concat(aPoints, bPoints, cPoints)) {
				tree.addPoint(aPoint.getPoint(), aPoint);
		}
		return tree;
	}
}