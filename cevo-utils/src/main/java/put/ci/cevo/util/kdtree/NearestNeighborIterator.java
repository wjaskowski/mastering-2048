package put.ci.cevo.util.kdtree;

import put.ci.cevo.util.math.DistanceMetric;

import java.util.Arrays;
import java.util.Iterator;

public class NearestNeighborIterator<T> implements Iterator<T>, Iterable<T> {

    private final DistanceMetric metric;
    private final double[] searchPoint;

    private final MinHeap<KDNode<T>> pendingPaths;
    private final IntervalHeap<T> evaluatedPoints;

    private int pointsRemaining;
    private double lastDistanceReturned;

    protected NearestNeighborIterator(KDNode<T> treeRoot, double[] searchPoint, int maxPointsReturned, DistanceMetric metric) {
        this.searchPoint = Arrays.copyOf(searchPoint, searchPoint.length);
        this.pointsRemaining = Math.min(maxPointsReturned, treeRoot.size());
        this.metric = metric;
        this.pendingPaths = new BinaryHeap.Min<>();
        this.pendingPaths.offer(0, treeRoot);
        this.evaluatedPoints = new IntervalHeap<T>();
    }

    @Override
    public boolean hasNext() {
        return pointsRemaining > 0;
    }

    @Override
    public T next() {
        if (!hasNext()) {
            throw new IllegalStateException("NearestNeighborIterator has reached end!");
        }

        while (pendingPaths.size() > 0 && (evaluatedPoints.size() == 0 || (pendingPaths.getMinKey() < evaluatedPoints.getMinKey()))) {
            KDTree.searchNeighbors(pendingPaths, evaluatedPoints, pointsRemaining, metric, searchPoint);
        }

        // Return the smallest distance point
        pointsRemaining--;
        lastDistanceReturned = evaluatedPoints.getMinKey();
        T value = evaluatedPoints.getMin();
        evaluatedPoints.removeMin();
        return value;
    }

    public double distance() {
        return lastDistanceReturned;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<T> iterator() {
        return this;
    }
}
