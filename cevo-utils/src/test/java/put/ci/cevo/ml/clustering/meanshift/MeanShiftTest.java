package put.ci.cevo.ml.clustering.meanshift;

import org.junit.Test;
import put.ci.cevo.ml.clustering.CentroidCluster;
import put.ci.cevo.ml.clustering.clusterable.Clusterable;
import put.ci.cevo.ml.clustering.clusterable.ClusterablePoint;
import put.ci.cevo.ml.clustering.meanshift.kernel.FlatKernel;
import put.ci.cevo.ml.clustering.meanshift.kernel.GaussianKernel;
import put.ci.cevo.util.Pair;
import put.ci.cevo.util.math.EuclideanDistance;
import put.ci.cevo.util.random.ThreadedContext;

import java.util.List;

import static com.google.common.collect.ImmutableList.of;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static put.ci.cevo.ml.clustering.meanshift.MeanShiftUtils.makeBlobs;

public class MeanShiftTest {

	public static final double EPS = 0.001;

	@Test
	public void testMeanShift() {
		ThreadedContext random = new ThreadedContext(123, 4);
		Pair<List<Clusterable>, List<Integer>> blobs = makeBlobs(1000,
				of(new ClusterablePoint(1, 1), new ClusterablePoint(-1, -1), new ClusterablePoint(1, -1)), 0.6, true, random.getRandomForThread());

		double bandwidth = MeanShiftUtils.estimateBandwidth(blobs.first(), 0.2, 100, random.getRandomForThread());
		assertEquals(0.9445823374425488, bandwidth, EPS);

		MeanShift<Clusterable> meanShift = new MeanShift<>(new GaussianKernel(), new EuclideanDistance(), bandwidth);

		List<CentroidCluster<Clusterable>> clusters = meanShift.cluster(blobs.first(), random);
		assertEquals(3, clusters.size());

		assertArrayEquals(new ClusterablePoint(0.9355921828887271, -0.8826555282415418).getPoint(),
				clusters.get(0).getCenter().getPoint(), EPS);
		assertEquals(339, clusters.get(0).getPoints().size());

		assertArrayEquals(new ClusterablePoint(-1.0194373051398684, -0.983789596998387).getPoint(),
				clusters.get(1).getCenter().getPoint(), EPS);
		assertEquals(330, clusters.get(1).getPoints().size());

		assertArrayEquals(new ClusterablePoint(0.9247381214268715, 0.9442266587978521).getPoint(),
				clusters.get(2).getCenter().getPoint(), EPS);
		assertEquals(332, clusters.get(2).getPoints().size());


	}

	@Test
	public void testKernels() {
		ThreadedContext random = new ThreadedContext(123, 4);
		Pair<List<Clusterable>, List<Integer>> blobs = makeBlobs(1000,
				of(new ClusterablePoint(1, 1), new ClusterablePoint(-1, -1), new ClusterablePoint(1, -1)), 0.6, true, random.getRandomForThread());

		double bandwidth = MeanShiftUtils.estimateBandwidth(blobs.first(), 0.2, 100, random.getRandomForThread());
		assertEquals(0.9445823374425488, bandwidth, EPS);

		MeanShift<Clusterable> flatShift = new MeanShift<>(new FlatKernel(), new EuclideanDistance(), bandwidth);
		MeanShift<Clusterable> gaussShift = new MeanShift<>(new GaussianKernel(), new EuclideanDistance(), bandwidth);

		List<CentroidCluster<Clusterable>> flatClusters = flatShift.cluster(blobs.first(), random);
		List<CentroidCluster<Clusterable>> gaussClusters = gaussShift.cluster(blobs.first(), random);

		assertEquals(3, flatClusters.size());
		assertEquals(341, flatClusters.get(0).getPoints().size());
		assertEquals(326, flatClusters.get(1).getPoints().size());
		assertEquals(334, flatClusters.get(2).getPoints().size());

		assertEquals(3, gaussClusters.size());
		assertEquals(339, gaussClusters.get(0).getPoints().size());
		assertEquals(330, gaussClusters.get(1).getPoints().size());
		assertEquals(332, gaussClusters.get(2).getPoints().size());
	}

}