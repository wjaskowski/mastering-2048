package put.ci.cevo.ml.clustering.algorithms;

import org.junit.Before;
import org.junit.Test;
import put.ci.cevo.ml.clustering.CentroidCluster;
import put.ci.cevo.ml.clustering.clusterable.Clusterable;
import put.ci.cevo.ml.clustering.clusterable.ClusterablePoint;
import put.ci.cevo.ml.clustering.meanshift.JSATMeanShiftAdapter;
import put.ci.cevo.ml.clustering.meanshift.MeanShift;
import put.ci.cevo.ml.clustering.meanshift.kernel.GaussianKernel;
import put.ci.cevo.ml.clustering.meanshift.seed.MeanSeed;
import put.ci.cevo.util.Pair;
import put.ci.cevo.util.math.EuclideanDistance;
import put.ci.cevo.util.random.ThreadedContext;

import java.util.List;

import static com.google.common.collect.ImmutableList.of;
import static org.junit.Assert.assertEquals;
import static put.ci.cevo.ml.clustering.meanshift.MeanShiftUtils.makeBlobs;

public class ClusteringTest {

	private ThreadedContext context;
	private Pair<List<Clusterable>, List<Integer>> blobs;

	@Before
	public void initObjects() {
		context = new ThreadedContext(123, 4);
		blobs = makeBlobs(1000, of(new ClusterablePoint(1, 1), new ClusterablePoint(-1, -1), new ClusterablePoint(1, -1)), 0.6, true,
				context.getRandomForThread());
	}

	@Test
	public void testKMedoidsClusterer() {
		KMedoidsClusterer km = new KMedoidsClusterer(3);
		List<CentroidCluster<Clusterable>> clusters = km.cluster(blobs.first(), context);
		assertEquals(3, clusters.size());
	}

	@Test
	public void testKMeansPlusPlusClusterer() {
		KMeansPlusPlusClusterer<Clusterable> km = new KMeansPlusPlusClusterer<>(3);
		List<CentroidCluster<Clusterable>> clusters = km.cluster(blobs.first(), context);
		assertEquals(3, clusters.size());
	}

	@Test
	public void testJSATMSClusterer() {
		JSATMeanShiftAdapter km = new JSATMeanShiftAdapter();
		List<CentroidCluster<Clusterable>> clusters = km.cluster(blobs.first(), context);
		assertEquals(3, clusters.size());
	}

	@Test
	public void testMyMeanShiftClusterer() {
		MeanShift<Clusterable> gaussShift = new MeanShift<>(new GaussianKernel(), new EuclideanDistance(), new MeanSeed());
		List<CentroidCluster<Clusterable>> clusters = gaussShift.cluster(blobs.first(), context);
		assertEquals(3, clusters.size());
	}

	@Test
	public void testJSATKmeansClusterer() {
		JSATKMeansClusterer km = new JSATKMeansClusterer(3);
		List<CentroidCluster<Clusterable>> clusters = km.cluster(blobs.first(), context);
		assertEquals(3, clusters.size());
	}

}
