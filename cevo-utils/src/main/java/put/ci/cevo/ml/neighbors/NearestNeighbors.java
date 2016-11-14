package put.ci.cevo.ml.neighbors;

import com.google.common.collect.Iterables;
import put.ci.cevo.ml.clustering.clusterable.Clusterable;
import put.ci.cevo.util.math.DistanceMetric;
import put.ci.cevo.util.Pair;
import put.ci.cevo.util.kdtree.KDTree;
import put.ci.cevo.util.kdtree.MaxHeap;
import put.ci.cevo.util.sequence.NullTerminatedSequence;
import put.ci.cevo.util.sequence.Sequence;

import static put.ci.cevo.util.Pair.create;

public class NearestNeighbors<T extends Clusterable> {

	private final KDTree<T> tree;
	private final DistanceMetric metric;

	public NearestNeighbors(Iterable<T> data, DistanceMetric metric) {
		this(createTree(data, Iterables.getFirst(data, null).getPoint().length), metric);
	}

	public NearestNeighbors(KDTree<T> tree, DistanceMetric metric) {
		this.tree = tree;
		this.metric = metric;
	}

	private static <T extends Clusterable> KDTree<T> createTree(Iterable<T> data, int dimensions) {
		KDTree<T> tree = new KDTree<>(dimensions);
		for (T point : data) {
			tree.addPoint(point.getPoint(), point);
		}
		return tree;
	}

	public Sequence<Pair<T, Double>> nearestWithDistance(T point, int maxNeighbors) {
		final MaxHeap<T> heap = tree.nearest(point.getPoint(), maxNeighbors, metric);
		return new NullTerminatedSequence<Pair<T, Double>>() {
			@Override
			protected Pair<T, Double> getNext() {
				if (heap.size() != 0) {
					Pair<T, Double> pair = create(heap.getMax(), heap.getMaxKey());
					heap.removeMax();
					return pair;
				}
				return null;
			}
		};
	}

	public Sequence<Pair<T, Double>> nearestWithDistance(T point, double maxDist) {
		final MaxHeap<T> heap = tree.nearest(point.getPoint(), maxDist, metric);
		return new NullTerminatedSequence<Pair<T, Double>>() {
			@Override
			protected Pair<T, Double> getNext() {
				if (heap.size() != 0) {
					Pair<T, Double> pair = create(heap.getMax(), heap.getMaxKey());
					heap.removeMax();
					return pair;
				}
				return null;
			}
		};
	}

	public Sequence<T> nearest(T point, double maxDist) {
		return nearest(point.getPoint(), maxDist);
	}

	public Sequence<T> nearest(double[] point, double maxDist) {
		final MaxHeap<T> heap = tree.nearest(point, maxDist, metric);
		return new NullTerminatedSequence<T>() {
			@Override
			protected T getNext() {
				if (heap.size() != 0) {
					T object = heap.getMax();
					heap.removeMax();
					return object;
				}
				return null;
			}
		};
	}

	public Sequence<T> nearest(T point, int maxNeighbors) {
		return nearest(point.getPoint(), maxNeighbors);
	}

	public Sequence<T> nearest(double[] point, int maxNeighbors) {
		final MaxHeap<T> heap = tree.nearest(point, maxNeighbors, metric);
		return new NullTerminatedSequence<T>() {
			@Override
			protected T getNext() {
				if (heap.size() != 0) {
					T object = heap.getMax();
					heap.removeMax();
					return object;
				}
				return null;
			}
		};
	}

}
