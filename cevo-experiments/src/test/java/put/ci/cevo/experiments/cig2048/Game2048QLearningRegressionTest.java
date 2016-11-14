package put.ci.cevo.experiments.cig2048;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.Assert;
import org.junit.Test;
import put.ci.cevo.games.game2048.Action2048;
import put.ci.cevo.rl.agent.functions.RealFunction;
import put.ci.cevo.rl.agent.functions.mlp.MLP;

@SuppressWarnings("unused")
public class Game2048QLearningRegressionTest {

	@Test
	public void testLearn() {
		RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(123));
		RealFunction[] functions = new RealFunction[4];
		for (int i = 0; i < 4; i++) {
			functions[i] = MLP.createRandomizedMLP(16, 3, 1, -0.1, 0.1, random);

		}

		Game2048QLearning game = new Game2048QLearning();
		ActionValueFunction2048 qFunction = new ActionValueFunction2048(functions);

		Assert.assertEquals("Initial performance", 1433.96, evaluatePerformance(game, qFunction,
				100, random), 10e-4);

		for (int i = 0; i < 100; i++) {
			game.QLearn(qFunction, 0.1, 0.0001, random);
		}

		Assert.assertEquals("Trained performance", 2498.4, evaluatePerformance(game, qFunction, 100, random), 10e-4);
	}

	private double evaluatePerformance(Game2048QLearning game, ActionValueFunction2048 qFunction, int numEpisodes,
			RandomDataGenerator random) {
		double performance = 0;
		for (int i = 0; i < numEpisodes; i++) {
			performance += game.play(qFunction, random).first();
		}
		return performance / numEpisodes;
	}

	@Test
	public void alwaysMoveUpTest() {
		Game2048QLearning game = new Game2048QLearning();
		RealFunction[] functions = new RealFunction[4];
		for (int i = 0; i < 4; i++) {
			functions[i] = new RealFunction() {
				@Override
				public void update(double[] input, double expectedValue, double learningRate) {
					// TODO Auto-generated method stub

				}

				@Override
				public double getValue(double[] input) {
					return 0;
				}
			};
		}

		functions[Action2048.UP.ordinal()] = new RealFunction() {
			@Override
			public void update(double[] input, double expectedValue, double learningRate) {
				// TODO Auto-generated method stub

			}

			@Override
			public double getValue(double[] input) {
				return 1;
			}
		};

		ActionValueFunction2048 qFunction = new ActionValueFunction2048(functions);
		RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(123));
		Assert.assertEquals(3120.0, evaluatePerformance(game, qFunction, 1, random), 10e-4);
	}
}
