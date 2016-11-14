package put.ci.cevo.util.ml.pca;

import static com.google.common.primitives.Doubles.toArray;
import static org.junit.Assert.assertArrayEquals;
import static put.ci.cevo.util.ArrayUtils.flatten;
import static put.ci.cevo.util.RandomUtils.sampleWithReplacement;

import java.util.List;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.Test;

import put.ci.cevo.ml.pca.PCA;
import put.ci.cevo.util.RandomUtils;

import com.google.common.collect.ImmutableList;

public class PCATest {

	@Test
	public void sampleToEigenSpace() {
		RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(123));
		int M = 30;
		int N = 5;

		double data[][] = new double[M][];
		for (int i = 0; i < M; i++) {
			data[i] = RandomUtils.randomDoubleVector(N, -0.1, 0.1, random);
		}

		PCA pca = new PCA();
		assertArrayEquals(flatten(data), flatten(pca.eigenToSampleSpace(pca.reduce(data, 1.0))), 0.000001);
		assertArrayEquals(flatten(data), flatten(pca.eigenToSampleSpace(pca.reduce(data, 5))), 0.000001);

	}

	@Test
	public void sampleToEigenSpace3() {
		RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(1));
		List<Double> vals = ImmutableList.of(1.0, 0.25, 0.5, 0.75, 0.0);
		double data[][] = new double[50][];
		for (int i = 0; i < 50; i++) {
			data[i] = toArray(sampleWithReplacement(vals, 50, random));
		}
		PCA pca = new PCA();
		double[] expected = new double[] { 0.33170006337445035, 0.06803422358902589, 0.7351469266181643,
			0.23687369635033478, 0.062109385879230705, 0.06999251212274099, 0.8514947953837426, 0.9474173796180849,
			0.3233614594691887, 0.46147347692563584, 0.14174513462976351, 0.026615699738294363, 0.8383420110893902,
			0.17616476955560567, 0.29991913952782745, 1.0274275311246375, 0.25045338927687066, 1.0476384653229904,
			0.2538716441507607, 0.8099226701950952, 0.6891114394448462, 0.3694830340008104, 0.48341183574617336,
			0.4848570265201874, 0.1337717598832165, 0.8797241617546763, 0.6079307165401846, 0.28568283407555484,
			0.024989839496546873, 0.30942618153802953, 0.13498669977335515, 0.6608244535478626, 0.5526728436338663,
			0.2514310004459882, 0.7146504319398853, 0.4245233059083131, 0.2991192640020255, 0.729923603789888,
			0.42082516064252107, 0.9544844224649851, 0.4602785494357523, 0.16676042368386157, 0.4358115078051257,
			0.3599637829341934, 0.4146266048005606, 0.2648663489062667, 0.14838671823954924, -0.04517606082129866,
			1.088462062057979, 0.3892514524788976 };
		assertArrayEquals(expected, pca.eigenToSampleSpace(pca.reduce(data, 0.8)[0]), 0.000001);
	}

}
