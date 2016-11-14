package put.ci.cevo.framework.interactions;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import put.ci.cevo.framework.algorithms.common.EffortTable;
import put.ci.cevo.framework.algorithms.common.PayoffTable;
import put.ci.cevo.util.random.ThreadedContext;

public class KRandomOpponentsInteractionSchemeTest {

	@Test
	public void testInteract() throws Exception {
		InteractionDomain<Integer, Integer> domain = (candidate, opponent, random) -> new InteractionResult(0, 0, 1);

		List<Integer> solutions = Arrays.asList(1, 2, 3);
		List<Integer> tests = Arrays.asList(1, 2, 3);
		ThreadedContext context = new ThreadedContext(123, 8);
		for (int k = 1; k <= 3; k++) {
			KRandomOpponentsInteractionScheme<Integer, Integer> scheme = new KRandomOpponentsInteractionScheme<>(
				domain, k);

			InteractionTable<Integer, Integer> table = scheme.interact(solutions, tests, context);
			PayoffTable<Integer, Integer> solutionsPayoffs = table.getSolutionsPayoffs();
			PayoffTable<Integer, Integer> testsPayoffs = table.getTestsPayoffs();
			EffortTable<Integer, Integer> efforts = table.getEfforts();

			for (Integer solution : solutions) {
				Assert.assertEquals(k, solutionsPayoffs.solutionPayoffs(solution).toList().size());
				Assert.assertEquals(k, testsPayoffs.testPayoffs(solution).toList().size());
				Assert.assertEquals(k, efforts.computeEffort(solution));
			}
		}
	}

}
