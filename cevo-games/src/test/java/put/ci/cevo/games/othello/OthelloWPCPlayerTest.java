package put.ci.cevo.games.othello;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.Assert;
import org.junit.Test;
import put.ci.cevo.games.GameOutcome;
import put.ci.cevo.games.PointPerPieceGameResultEvaluator;
import put.ci.cevo.games.othello.players.OthelloWPCPlayer;
import put.ci.cevo.games.othello.players.published.OthelloStandardWPCHeuristicPlayer;
import put.ci.cevo.games.othello.players.published.SzubertJaskowskiKrawiec2009Player;

public class OthelloWPCPlayerTest {

	@Test
	public void testOthelloWPCPlayerWPCDouble() throws Exception {
		Othello othello = new Othello(new PointPerPieceGameResultEvaluator());
		Othello randomizedOthello = new Othello(new PointPerPieceGameResultEvaluator(), 0.2, 0.2, Integer.MAX_VALUE);

		OthelloWPCPlayer opponent = new SzubertJaskowskiKrawiec2009Player().create();

		OthelloWPCPlayer standard = new OthelloStandardWPCHeuristicPlayer().create();

		RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(123));
		GameOutcome result1 = othello.play(standard, opponent, random);
		GameOutcome result2 = randomizedOthello.play(standard, opponent, random);

		// Randomized player should (most probably) return a different game
		Assert.assertNotEquals(result1, result2);
	}
}
