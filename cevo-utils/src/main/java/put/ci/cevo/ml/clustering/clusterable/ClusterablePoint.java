package put.ci.cevo.ml.clustering.clusterable;

import put.ci.cevo.util.Point2D;

public class ClusterablePoint implements Clusterable {

	private final Point2D point;

	public ClusterablePoint(double x, double y) {
		this(new Point2D(x, y));
	}

	public ClusterablePoint(Point2D point) {
		this.point = point;
	}

	public double getX() {
		return point.getX();
	}

	public double getY() {
		return point.getY();
	}

	@Override
	public double[] getPoint() {
		return new double[] { point.getX(), point.getY() };
	}

	@Override
	public int hashCode() {
		return point.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ClusterablePoint other = (ClusterablePoint) obj;
		return point.equals(other.point);
	}

	@Override
	public String toString() {
		return "Point{" +
				"x=" + point.getX() +
				", y=" + point.getY() +
				'}';
	}

}
