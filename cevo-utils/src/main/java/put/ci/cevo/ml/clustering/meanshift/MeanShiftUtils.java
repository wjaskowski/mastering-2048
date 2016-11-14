package put.ci.cevo.ml.clustering.meanshift;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.ml.clustering.clusterable.Clusterable;
import put.ci.cevo.ml.clustering.clusterable.ClusterablePoint;
import put.ci.cevo.ml.neighbors.NearestNeighbors;
import put.ci.cevo.util.Pair;
import put.ci.cevo.util.RandomUtils;
import put.ci.cevo.util.math.EuclideanDistance;
import put.ci.cevo.util.sequence.transforms.Transforms;

import java.util.ArrayList;
import java.util.List;

import static put.ci.cevo.util.IntArrays.range;
import static put.ci.cevo.util.RandomUtils.shuffleInts;
import static put.ci.cevo.util.sequence.Sequences.seq;
import static put.ci.cevo.util.sequence.aggregates.Aggregates.intAdd;

public class MeanShiftUtils {

	public static <T extends Clusterable> double estimateBandwidth(List<T> points, RandomDataGenerator random) {
		return estimateBandwidth(points, points.size(), random);
	}

	public static <T extends Clusterable> double estimateBandwidth(List<T> points, int samples,
			RandomDataGenerator random) {
		return estimateBandwidth(points, 0.3, samples, random);
	}

	/**
	 * Estimates the bandwidth to be used with the mean-shift algorithm. Takes time at least quadratic in samples.
	 * For large datasets, it's wise to set that parameter to a small value.
	 */
	public static <T extends Clusterable> double estimateBandwidth(List<T> points, double quantile, int samples,
			RandomDataGenerator random) {
		if (samples != points.size()) {
			points = RandomUtils.sample(points, samples, random);
		}

		NearestNeighbors<T> neighbors = new NearestNeighbors<>(points, new EuclideanDistance());
		int numNeighbors = (int) (points.size() * quantile);

		double bandwidth = 0;
		for (T point : points) {
			bandwidth += neighbors.nearestWithDistance(point, numNeighbors)
					.map(Transforms.<Double>getSecond()).reduce(Transforms.<Double>max());
		}
		return bandwidth / points.size();
	}

	/**
	 * Generates isotropic Gaussian blobs for clustering.
	 */
	public static Pair<List<Clusterable>, List<Integer>> makeBlobs(int numSamples, List<ClusterablePoint> centers, double sigma,
			boolean shuffle, RandomDataGenerator random) {
		int numCentres = centers.size();
		int[] numSamplesPerCenter = new int[numCentres];

		for (int i = 0; i < numCentres; i++) {
			numSamplesPerCenter[i] = numSamples / numCentres;
		}

		for (int i : range(0, numSamples % numCentres)) {
			numSamplesPerCenter[i] += 1;
		}

		int numPoints = seq(numSamplesPerCenter).aggregate(intAdd());
		List<Clusterable> x = new ArrayList<>(numPoints);
		List<Integer> y = new ArrayList<>(numPoints);
		for (int i = 0; i < numCentres; i++) {
			ClusterablePoint center = centers.get(i);
			for (int j = 0; j < numSamplesPerCenter[i]; j++) {
				double newX = center.getX() + random.nextGaussian(0, sigma);
				double newY = center.getY() + random.nextGaussian(0, sigma);
				x.add(new ClusterablePoint(newX, newY));
				y.add(i);
			}
		}

		if (shuffle) {
			List<Clusterable> tempX = new ArrayList<>(numPoints);
			List<Integer> tempY = new ArrayList<>(numPoints);

			int[] indicies = shuffleInts(range(0, numPoints - 1), random);
			for (int i : indicies) {
				tempX.add(x.get(i));
				tempY.add(y.get(i));
			}

			x = tempX;
			y = tempY;
		}
		return Pair.create(x, y);
	}
}
