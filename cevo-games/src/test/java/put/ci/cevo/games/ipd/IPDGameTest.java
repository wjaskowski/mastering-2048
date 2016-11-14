package put.ci.cevo.games.ipd;

import static org.junit.Assert.assertEquals;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.Test;

import put.ci.cevo.games.GameOutcome;
import put.ci.cevo.games.PointPerPieceGameResultEvaluator;

public class IPDGameTest {

	@Test
	public void threeChoicesIPDTest() {
		final IteratedPrisonersDilemma ipd = new IteratedPrisonersDilemma(
			new LinearPayoffInterpolator(3), 200, new PointPerPieceGameResultEvaluator());

		int[] first = new int[] { 0, 1, 1, 1, 2, 2, 2, 1, 2, 1 };
		int[] second = new int[] { 2, 2, 0, 2, 0, 1, 0, 1, 2, 2 };

		IPDPlayer player = new IPDPlayer(first);
		IPDPlayer opponent = new IPDPlayer(second);

		RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(new MersenneTwister(1).nextLong()));
		GameOutcome outcome = ipd.play(player, opponent, random);

		assertEquals(842.5, outcome.playerPoints(), 0.0001);
		assertEquals(600.0, outcome.opponentPoints(), 0.0001);
	}

	@Test
	public void fiveChoicesIPDTest() {
		final IteratedPrisonersDilemma ipd = new IteratedPrisonersDilemma(
			new LinearPayoffInterpolator(5), 200, new PointPerPieceGameResultEvaluator());

		int[] first = new int[] { 1, 0, 1, 2, 3, 3, 4, 4, 3, 2, 1, 0, 1, 2, 3, 3, 4, 4, 3, 2, 2, 2, 2, 4, 4, 2 };
		int[] second = new int[] { 3, 4, 1, 4, 1, 2, 0, 2, 3, 4, 3, 3, 1, 3, 1, 2, 1, 2, 3, 3, 4, 0, 4, 3, 4, 2 };

		IPDPlayer player = new IPDPlayer(first);
		IPDPlayer opponent = new IPDPlayer(second);

		RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(new MersenneTwister(1).nextLong()));
		GameOutcome outcome = ipd.play(player, opponent, random);

		assertEquals(674.5, outcome.playerPoints(), 0.0001);
		assertEquals(548.25, outcome.opponentPoints(), 0.0001);
	}

}
