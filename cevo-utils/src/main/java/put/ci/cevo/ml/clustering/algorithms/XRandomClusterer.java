package put.ci.cevo.ml.clustering.algorithms;

import com.google.common.collect.ImmutableList;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.ml.clustering.Cluster;
import put.ci.cevo.ml.clustering.Clusterer;
import put.ci.cevo.ml.clustering.clusterable.Clusterable;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.sequence.transforms.LazyMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static put.ci.cevo.ml.clustering.algorithms.XMeansClusterer.NumClusters;
import static put.ci.cevo.util.RandomUtils.nextInt;

public class XRandomClusterer<T extends Clusterable> implements Clusterer<T> {

	private static final int DEFAULT_MIN_K = 1;
	private static final int DEFAULT_MAX_K = 5;

	private final int minK;
	private final int maxK;

	public XRandomClusterer() {
		this(DEFAULT_MIN_K, DEFAULT_MAX_K);
	}

	public XRandomClusterer(int minK, int maxK) {
		this.minK = minK;
		this.maxK = maxK;
	}

	@Override
	public List<Cluster<T>> cluster(Collection<T> points, ThreadedContext context) {
		Map<Integer, Cluster<T>> clusters = new LazyMap<Integer, Cluster<T>>() {
			@Override
			protected Cluster<T> transform(Integer v) {
				return new Cluster<>();
			}
		};

		RandomDataGenerator random = context.getRandomForThread();
		int k = random.nextInt(minK, maxK);
		context.getEventsLogger().log(NumClusters.class, k);
		for (T point : points) {
			clusters.get(nextInt(0, k - 1, random)).addPoint(point);
		}
		return ImmutableList.copyOf(clusters.values());
	}

}