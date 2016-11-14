package put.ci.cevo.games.othello;

import org.junit.Assert;
import org.junit.Test;

public class LucasInitialOthelloStatesTest {

	@Test
	public void testBoardsRegression() throws Exception {
		Assert.assertEquals(new LucasInitialOthelloStates().boards().size(), 857);
	}
}