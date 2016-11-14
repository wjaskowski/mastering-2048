package put.ci.cevo.games.othello;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.Assert;
import org.junit.Test;
import put.ci.cevo.games.GameOutcome;
import put.ci.cevo.games.PointPerPieceGameResultEvaluator;
import put.ci.cevo.games.othello.players.OthelloPlayer;
import put.ci.cevo.games.othello.players.published.LucasRunnarson2006Player;
import put.ci.cevo.games.othello.players.published.SzubertJaskowskiKrawiec2013CTDLPlayer;

public class OthelloNTuplesRegressionTest {

	@Test
	public void testEvaluateMove() throws Exception {
		RandomDataGenerator mainRandom = new RandomDataGenerator(new MersenneTwister(123));

		OthelloPlayer player1 = new LucasRunnarson2006Player().create();
		OthelloPlayer player2 = new SzubertJaskowskiKrawiec2013CTDLPlayer().create();

		List<GameOutcome> outcomes = new ArrayList<>();
		for (int i = 0; i < 16; ++i) {
			int seed = mainRandom.getRandomGenerator().nextInt();
			RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(seed));

			DoubleOthello othello = new DoubleOthello(new PointPerPieceGameResultEvaluator());

			outcomes.add(othello.play(player1, player2, random));
		}

		List<GameOutcome> expected = Arrays.asList(
				new GameOutcome(13, 51),
				new GameOutcome(16.5, 47.5),
				new GameOutcome(22.5, 41.5),
				new GameOutcome(17, 47),
				new GameOutcome(16.5, 47.5),
				new GameOutcome(13.0, 51.0),
				new GameOutcome(13.0, 51.0),
				new GameOutcome(22.5, 41.5),
				new GameOutcome(11.5, 52.5),
				new GameOutcome(13.0, 51.0),
				new GameOutcome(22.5, 41.5),
				new GameOutcome(22.5, 41.5),
				new GameOutcome(13.0, 51.0),
				new GameOutcome(9.5, 54.5),
				new GameOutcome(17.0, 47.0),
				new GameOutcome(13.0, 51.0));

		Assert.assertArrayEquals(expected.toArray(), outcomes.toArray());
	}
}
