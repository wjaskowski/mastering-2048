package put.ci.cevo.util.kdtree;

import put.ci.cevo.ml.clustering.clusterable.Clusterable;
import put.ci.cevo.util.math.DistanceMetric;

import java.util.List;

import static java.lang.Math.min;

public final class KDTree<T> {

	private final KDNode<T> root;

	public KDTree(int dimensions) {
		this(dimensions, 24);
	}

	public KDTree(int dimensions, int bucketCapacity) {
		this.root = new KDNode<>(dimensions, bucketCapacity);
	}

	public void addPoint(double[] point, T value) {
		root.addPoint(point, value);
	}

	public NearestNeighborIterator<T> getNearestNeighborIterator(double[] point, int maxPoints, DistanceMetric metric) {
		return new NearestNeighborIterator<>(root, point, maxPoints, metric);
	}

	public MaxHeap<T> nearest(double[] point, double maxDist, DistanceMetric metric) {
		final BinaryHeap.Min<KDNode<T>> paths = new BinaryHeap.Min<>();
		final BinaryHeap.Max<T> points = new BinaryHeap.Max<>();

		paths.offer(0, root);

		do {
			searchNeighbors(paths, points, maxDist, metric, point);
		} while (paths.size() > 0 && (paths.getMinKey() < maxDist));

		return points;
	}

	public MaxHeap<T> nearest(double[] searchPoint, int maxPoints, DistanceMetric metric) {
		final BinaryHeap.Min<KDNode<T>> paths = new BinaryHeap.Min<>();
		final BinaryHeap.Max<T> points = new BinaryHeap.Max<>();

		final int remaining = min(maxPoints, root.size());
		paths.offer(0, root);

		while (paths.size() > 0 && (points.size() < remaining || (paths.getMinKey() < points.getMaxKey()))) {
			searchNeighbors(paths, points, remaining, metric, searchPoint);
		}

		return points;
	}

	protected static <T> void searchNeighbors(MinHeap<KDNode<T>> pendingPaths, MaxHeap<T> evaluatedPoints,
			int desiredPoints, DistanceMetric metric, double[] searchPoint) {
		// If there are pending paths possibly closer than the nearest evaluated point, check it out
		KDNode<T> cursor = pendingPaths.getMin();
		pendingPaths.removeMin();

		// Descend the tree, recording paths not taken
		while (!cursor.isLeaf()) {
			KDNode<T> pathNotTaken;
			if (searchPoint[cursor.splitDimension] > cursor.splitValue) {
				pathNotTaken = cursor.left;
				cursor = cursor.right;
			} else {
				pathNotTaken = cursor.right;
				cursor = cursor.left;
			}
			double otherDistance = metric.distance(searchPoint, findRectPoint(searchPoint, pathNotTaken.minBound, pathNotTaken.maxBound));
			// Only add a path if we either need more points or it's closer than furthest point on list so far
			if (evaluatedPoints.size() < desiredPoints || otherDistance <= evaluatedPoints.getMaxKey()) {
				pendingPaths.offer(otherDistance, pathNotTaken);
			}
		}

		if (cursor.singlePoint) {
			double nodeDistance = metric.distance(cursor.points[0], searchPoint);
			// Only add a point if either need more points or it's closer than furthest on list so far
			if (evaluatedPoints.size() < desiredPoints || nodeDistance <= evaluatedPoints.getMaxKey()) {
				for (int i = 0; i < cursor.size(); i++) {
					T value = (T) cursor.data[i];

					// If we don't need any more, replace max
					if (evaluatedPoints.size() == desiredPoints) {
						evaluatedPoints.replaceMax(nodeDistance, value);
					} else {
						evaluatedPoints.offer(nodeDistance, value);
					}
				}
			}
		} else {
			// Add the points at the cursor
			for (int i = 0; i < cursor.size(); i++) {
				double[] point = cursor.points[i];
				T value = (T) cursor.data[i];
				double distance = metric.distance(point, searchPoint);
				// Only add a point if either need more points or it's closer than furthest on list so far
				if (evaluatedPoints.size() < desiredPoints) {
					evaluatedPoints.offer(distance, value);
				} else if (distance < evaluatedPoints.getMaxKey()) {
					evaluatedPoints.replaceMax(distance, value);
				}
			}
		}
	}

	protected static <T> void searchNeighbors(MinHeap<KDNode<T>> pendingPaths, MaxHeap<T> evaluatedPoints,
			double maxDist, DistanceMetric metric, double[] searchPoint) {
		// If there are pending paths possibly closer than the nearest evaluated point, check it out
		KDNode<T> cursor = pendingPaths.getMin();
		pendingPaths.removeMin();

		// Descend the tree, recording paths not taken
		while (!cursor.isLeaf()) {
			KDNode<T> pathNotTaken;
			if (searchPoint[cursor.splitDimension] > cursor.splitValue) {
				pathNotTaken = cursor.left;
				cursor = cursor.right;
			} else {
				pathNotTaken = cursor.right;
				cursor = cursor.left;
			}
			double otherDistance = metric.distance(searchPoint, findRectPoint(searchPoint, pathNotTaken.minBound, pathNotTaken.maxBound));
			// Only add a path if we either need more points or it's closer than furthest point on list so far
			if (otherDistance <= maxDist) {
				pendingPaths.offer(otherDistance, pathNotTaken);
			}
		}

		if (cursor.singlePoint) {
			double nodeDistance = metric.distance(cursor.points[0], searchPoint);
			// Only add a point if either need more points or it's closer than furthest on list so far
			if (nodeDistance <= maxDist) {
				for (int i = 0; i < cursor.size(); i++) {
					T value = (T) cursor.data[i];
					evaluatedPoints.offer(nodeDistance, value);
				}
			}
		} else {
			// Add the points at the cursor
			for (int i = 0; i < cursor.size(); i++) {
				double[] point = cursor.points[i];
				T value = (T) cursor.data[i];
				double distance = metric.distance(point, searchPoint);
				// Only add a point if either need more points or it's closer than furthest on list so far
				if (distance <= maxDist) {
					evaluatedPoints.offer(distance, value);
				}
			}
		}
	}

	/**
	 * Returns a nearest point on the rectangle specified by min and max to a given point.
	 */
	private static double[] findRectPoint(double[] point, double[] min, double[] max) {
		double[] rectPoint = new double[point.length];

		for (int i = 0; i < point.length; i++) {
			if (point[i] > max[i]) {
				rectPoint[i] = max[i];
			} else if (point[i] < min[i]) {
				rectPoint[i] = min[i];
			} else {
				rectPoint[i] = point[i];
			}
		}

		return rectPoint;
	}

	public static <T extends Clusterable> KDTree<T> createTree(Iterable<T> points, int dimensionality) {
		KDTree<T> tree = new KDTree<>(dimensionality);
		for (T point : points) {
			tree.addPoint(point.getPoint(), point);
		}
		return tree;
	}

	public static <T extends Clusterable> KDTree<T> createTree(List<T> points) {
		KDTree<T> tree = new KDTree<>(points.get(0).getPoint().length, points.size());
		for (T point : points) {
			tree.addPoint(point.getPoint(), point);
		}
		return tree;
	}

}
