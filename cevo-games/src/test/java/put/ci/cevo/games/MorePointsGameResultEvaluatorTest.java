package put.ci.cevo.games;

import org.junit.Assert;
import org.junit.Test;

public class MorePointsGameResultEvaluatorTest {

	@Test
	public void testMorePointsGameResultEvaluator() throws Exception {
		MorePointsGameResultEvaluator evaluator = new MorePointsGameResultEvaluator(10, -10, 3);

		{
			GameOutcome outcome = evaluator.evaluate(300, 100);
			GameOutcome expected = new GameOutcome(10, -10);
			Assert.assertEquals(expected, outcome);
		}

		{
			GameOutcome outcome = evaluator.evaluate(99, 100);
			GameOutcome expected = new GameOutcome(-10, 10);
			Assert.assertEquals(expected, outcome);
		}

		{
			GameOutcome outcome = evaluator.evaluate(-13, -13);
			GameOutcome expected = new GameOutcome(3, 3);
			Assert.assertEquals(expected, outcome);
		}

	}

}
