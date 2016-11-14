package put.ci.cevo.framework.algorithms;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.Test;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.vectors.DoubleVector;

public class SharkVDCMAESInternalRegressionTest {

	private final double[] INITIAL_POINT = new double[] { -10, 10, -10, 10, -10, 10, -5, 5, -10, 10 };

	private final double INITIAL_SIGMA = 1 / Math.sqrt(INITIAL_POINT.length);

	private static double maxSquares(DoubleVector x) {
		return x.stream().map(a -> a * a).max().getAsDouble();
	}

	@Test
	public void testVDCMAESShark() throws Exception {
		ThreadedContext random = new ThreadedContext(123);
		int POP_SIZE = 10;
		SharkVDCMAESInternal shark = new SharkVDCMAESInternal(
				INITIAL_POINT,
				POP_SIZE,
				SharkVDCMAESInternal.suggestMu(POP_SIZE),
				INITIAL_SIGMA,
				random.getRandomForThread());

		Function<List<double[]>, List<Double>> populationEvaluator = pop -> pop.stream().map(DoubleVector::of).map(
				SharkVDCMAESInternalRegressionTest::maxSquares).collect(Collectors.toList());

		for (int i = 0; i < 50; ++i) {
			shark.step(populationEvaluator, random.getRandomForThread());
		}
		assertEquals(1.76613, maxSquares(DoubleVector.of(shark.currentBest())), 0.0001);
	}
}