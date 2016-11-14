package put.ci.cevo.ml.clustering;

import put.ci.cevo.ml.clustering.clusterable.Clusterable;

import java.util.ArrayList;
import java.util.List;

public class Cluster<T extends Clusterable> {

	private final List<T> points;

	public Cluster() {
		points = new ArrayList<>();
	}

	public Cluster<T> addPoints(Iterable<T> points) {
		for (T point : points) {
			addPoint(point);
		}
		return this;
	}

	public Cluster<T> addPoint(final T point) {
		points.add(point);
		return this;
	}

	public List<T> getPoints() {
		return points;
	}

}
