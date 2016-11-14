package put.ci.cevo.games.dct;

import org.junit.Assert;
import org.junit.Test;

import put.ci.cevo.games.GameOutcome;

public class DensityClassificationTaskTest {

	@Test
	public void testSimpleDCT() {
		DensityClassificationTask dct = new DensityClassificationTask(1, 1);
		CARule rule = new CARule(new int[] { 0, 0, 0, 1, 0, 1, 1, 1 });
		CAConfiguration test = new CAConfiguration(new int[] { 1, 1, 0, 1 });
		GameOutcome play = dct.play(rule, test, null);
		Assert.assertEquals(1, (int) play.playerPoints());
	}
}
