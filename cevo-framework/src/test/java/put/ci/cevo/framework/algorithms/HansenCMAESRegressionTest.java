package put.ci.cevo.framework.algorithms;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import put.ci.cevo.framework.evaluators.OneByOnePopulationEvaluator;
import put.ci.cevo.framework.operators.IdentityAdapter;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.framework.state.EvolutionState;
import put.ci.cevo.framework.termination.GenerationsTarget;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.vectors.DoubleVector;

public class HansenCMAESRegressionTest {

	private static double averageOfSquares(DoubleVector x) {
		return x.stream().map(a -> a * a).average().getAsDouble();
	}

	@Test
	public void CMAESRegression() throws Exception {
		HansenCMAESAlgorithm<DoubleVector> cmaes = new HansenCMAESAlgorithm<>(
				10,
				new OneByOnePopulationEvaluator<>(
						(DoubleVector individual, ThreadedContext context) -> new EvaluatedIndividual<>(individual,
								-averageOfSquares(individual))),
				DoubleVector.of(new double[] { -10, 10, -10, 10, -10, 10, -5, 5, -10, 10 }),
				new IdentityAdapter<>(),
				1 / Math.sqrt(10),
				false,  // sepCMAES
				false); // constant sigma

		cmaes.addLastGenerationListener((EvolutionState state) -> {
			assertEquals(0.34436, averageOfSquares(state.<DoubleVector>getBestSolution().getIndividual()),
					0.0001);
		});
		cmaes.evolve(new GenerationsTarget(50), new ThreadedContext(123));
	}

	@Test
	public void sepCMAESRegression() throws Exception {
		HansenCMAESAlgorithm<DoubleVector> cmaes = new HansenCMAESAlgorithm<>(
				10,
				new OneByOnePopulationEvaluator<>(
						(DoubleVector individual, ThreadedContext context) -> new EvaluatedIndividual<>(individual,
								-averageOfSquares(individual))),
				DoubleVector.of(new double[] { -10, 10, -10, 10, -10, 10, -5, 5, -10, 10 }),
				new IdentityAdapter<>(),
				1 / Math.sqrt(10),
				true,   // sepCMAES
				false); // constant sigma

		cmaes.addLastGenerationListener((EvolutionState state) -> {
			assertEquals(0.057822, averageOfSquares(state.<DoubleVector>getBestSolution().getIndividual()),
					0.0001);
		});
		cmaes.evolve(new GenerationsTarget(50), new ThreadedContext(123));
	}
}