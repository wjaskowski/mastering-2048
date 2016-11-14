package put.ci.cevo.ml.clustering.algorithms;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import jsat.linear.distancemetrics.EuclideanDistance;
import org.junit.Before;
import org.junit.Test;
import put.ci.cevo.ml.clustering.clusterable.Clusterable;
import put.ci.cevo.ml.clustering.clusterable.ClusterablePoint;
import put.ci.cevo.ml.clustering.meanshift.JSATMeanShiftAdapter;
import put.ci.cevo.ml.clustering.meanshift.MeanShift;
import put.ci.cevo.ml.clustering.meanshift.kernel.GaussianKernel;
import put.ci.cevo.util.Pair;
import put.ci.cevo.util.random.ThreadedContext;

import java.util.List;

import static com.google.common.collect.ImmutableList.of;
import static put.ci.cevo.ml.clustering.meanshift.MeanShiftUtils.makeBlobs;

public class ClusteringBenchmark extends AbstractBenchmark {

	private ThreadedContext context;
	private Pair<List<Clusterable>, List<Integer>> blobs;

	@Before
	public void initObjects() {
		context = new ThreadedContext(123, 4);
		blobs = makeBlobs(100, of(new ClusterablePoint(1, 1), new ClusterablePoint(-1, -1), new ClusterablePoint(1, -1)), 0.6, true,
				context.getRandomForThread());
	}

	@BenchmarkOptions(benchmarkRounds = 5, warmupRounds = 1)
	@Test
	public void testMeanShiftPerformance() {
		MeanShift<Clusterable> ms = new MeanShift<>(new GaussianKernel(),
				new put.ci.cevo.util.math.EuclideanDistance());
		ms.cluster(blobs.first(), context);
	}

	@BenchmarkOptions(benchmarkRounds = 5, warmupRounds = 1)
	@Test
	public void testJSATMeanShiftPerformance() {
		JSATMeanShiftAdapter ms = new JSATMeanShiftAdapter(new EuclideanDistance());
		ms.cluster(blobs.first(), context);
	}

	@BenchmarkOptions(benchmarkRounds = 5, warmupRounds = 1)
	@Test
	public void testKMedoidsPerformance() {
		KMedoidsClusterer c = new KMedoidsClusterer(3);
		c.cluster(blobs.first(), context);
	}

}
