package put.ci.cevo.ml.clustering.meanshift;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.ml.clustering.*;
import put.ci.cevo.ml.clustering.clusterable.Clusterable;
import put.ci.cevo.ml.clustering.clusterable.ClusterableVector;
import put.ci.cevo.ml.neighbors.NearestNeighbors;
import put.ci.cevo.util.math.DistanceMetric;
import put.ci.cevo.ml.clustering.meanshift.kernel.Kernel;
import put.ci.cevo.ml.clustering.meanshift.seed.PointSeed;
import put.ci.cevo.ml.clustering.meanshift.seed.SeedingStrategy;
import put.ci.cevo.util.MapUtils;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.sequence.transforms.LazyMap;
import put.ci.cevo.util.sequence.transforms.Transform;
import put.ci.cevo.util.sequence.transforms.Transforms;
import put.ci.cevo.util.vectors.DoubleVector;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.collect.Iterables.getOnlyElement;
import static java.lang.Double.NEGATIVE_INFINITY;
import static put.ci.cevo.ml.clustering.meanshift.MeanShiftUtils.estimateBandwidth;
import static put.ci.cevo.util.sequence.Sequences.seq;

public class MeanShift<T extends Clusterable> implements Clusterer<T> {

	private static final int DEFAULT_MAX_ITERATIONS = 300;
	private static final double STOP_THRESH = 1e-5;

	private final Kernel kernel;
	private final SeedingStrategy initSeeds;
	private final DistanceMetric metric;

	private final double window;
	private final int iterations;

	public MeanShift(Kernel kernel, DistanceMetric metric) {
		this(kernel, metric, NEGATIVE_INFINITY, DEFAULT_MAX_ITERATIONS, new PointSeed());
	}
	public MeanShift(Kernel kernel, DistanceMetric metric, SeedingStrategy seed) {
		this(kernel, metric, NEGATIVE_INFINITY, DEFAULT_MAX_ITERATIONS, seed);
	}

	public MeanShift(Kernel kernel, DistanceMetric metric, double window) {
		this(kernel, metric, window, DEFAULT_MAX_ITERATIONS, new PointSeed());
	}

	public MeanShift(Kernel kernel, DistanceMetric metric, int iterations) {
		this(kernel, metric, NEGATIVE_INFINITY, iterations, new PointSeed());
	}

	public MeanShift(Kernel kernel, DistanceMetric metric, double window, int iterations, SeedingStrategy initSeeds) {
		this.kernel = kernel;
		this.metric = metric;
		this.window = window;
		this.iterations = iterations;
		this.initSeeds = initSeeds;
	}

	@Override
	public List<CentroidCluster<T>> cluster(Collection<T> points, ThreadedContext context) {
		final List<ClusterableVector> vectors = pointsToVectors(points);
		final double bandwidth = getBandwidth(vectors, context.getRandomForThread());

		final NearestNeighbors<ClusterableVector> nbrs = new NearestNeighbors<>(vectors, metric);
		final Map<ClusterableVector, Integer> densities = new ConcurrentHashMap<>();

		// For each seed, climb gradient until convergence or max iterations
		context.submit(new ThreadedContext.Worker<ClusterableVector, Void>() {
			@Override
			public Void process(ClusterableVector seed, ThreadedContext context) throws Exception {
				meanShift(bandwidth, nbrs, densities, seed);
				return null;
			}
		}, initSeeds.getSeeds(vectors, bandwidth, context.getRandomForThread()));

		// Merge kernels which are close to each other (less than bandwidth)
		Set<ClusterableVector> clusterCenters = mergeClusters(bandwidth, densities);

		// Assign points to centroids
		return getCentroidClusters(points, clusterCenters);
	}

	private List<ClusterableVector> pointsToVectors(Collection<T> points) {
		return seq(points).map(new Transform<T, ClusterableVector>() {
			@Override
			public ClusterableVector transform(T object) {
				return new ClusterableVector(object.getPoint());
			}
		}).toList();
	}

	private double getBandwidth(List<ClusterableVector> points, RandomDataGenerator random) {
		if (window == NEGATIVE_INFINITY) {
			return estimateBandwidth(points, random);
		}
		return window;
	}

	private void meanShift(double bandwidth, NearestNeighbors<ClusterableVector> nbrs, Map<ClusterableVector, Integer> densities,
			ClusterableVector seed) {
		int iters = 0;
		while (true) {
			// Find mean of the points within the bandwidth
			List<ClusterableVector> neighbors = nbrs.nearestWithDistance(seed, bandwidth)
					.map(Transforms.<ClusterableVector> getFirst()).toList();

			// Depending on seeding strategy this may occur
			if (neighbors.size() < 1) {
				break;
			}

			DoubleVector prev = new DoubleVector(seed.getPoint());
			seed = kernel.computeKernel(seed, neighbors, bandwidth);

			// If converged or at max iterations, stop and update
			if (seed.getVector().subtract(prev).getL1Norm() < STOP_THRESH || iters == iterations) {
				densities.put(seed, neighbors.size());
				break;
			}
			iters++;
		}
	}

	private Set<ClusterableVector> mergeClusters(double bandwidth, Map<ClusterableVector, Integer> centerIntesities) {
		Set<ClusterableVector> sortedCenters = MapUtils.sortByValueDescending(centerIntesities).keySet();
		Set<ClusterableVector> clusterCenters = new LinkedHashSet<>(sortedCenters);
		NearestNeighbors<ClusterableVector> nbrs = new NearestNeighbors<>(sortedCenters, metric);

		for (ClusterableVector vector : sortedCenters) {
			if (clusterCenters.contains(vector)) {
				clusterCenters.removeAll(nbrs.nearest(vector, bandwidth).asCollection());
				clusterCenters.add(vector);
			}
		}
		return clusterCenters;
	}

	private List<CentroidCluster<T>> getCentroidClusters(Collection<T> points, Set<ClusterableVector> clusterCenters) {
		Map<ClusterableVector, CentroidCluster<T>> clusters = new LazyMap<ClusterableVector, CentroidCluster<T>>(new LinkedHashMap<>()) {
			@Override
			protected CentroidCluster<T> transform(ClusterableVector vector) {
				return new CentroidCluster<>(vector);
			}
		};
		NearestNeighbors<ClusterableVector> nbrs = new NearestNeighbors<>(clusterCenters, metric);
		for (T point : points) {
			clusters.get(getOnlyElement(nbrs.nearest(point.getPoint(), 1))).addPoint(point);
		}
		return new ArrayList<>(clusters.values());
	}

}
