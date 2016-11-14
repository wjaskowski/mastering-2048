package put.ci.cevo.experiments.new2048;

import java.util.List;
import java.util.function.Consumer;

import put.ci.cevo.framework.measures.PerformanceMeasure;
import put.ci.cevo.games.game2048.Action2048;
import put.ci.cevo.games.game2048.Game2048;
import put.ci.cevo.games.game2048.Game2048Outcome;
import put.ci.cevo.games.game2048.State2048;
import put.ci.cevo.rl.agent.Agent;
import put.ci.cevo.util.CollectionUtils;
import put.ci.cevo.util.RandomFactory;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.random.ThreadedContext;

/** Measures the number of points obtained */
public class Game2048PerformanceMeasure implements PerformanceMeasure<Agent<State2048, Action2048>> {

	private final int sampleSize;
	private final Consumer<Game2048Outcome> gamePlayedListener;

	private final Game2048 game = new Game2048();

	@AccessedViaReflection
	public Game2048PerformanceMeasure(int sampleSize, Consumer<Game2048Outcome> gamePlayedListener) {
		this.gamePlayedListener = gamePlayedListener;
		this.sampleSize = sampleSize;
	}

	@AccessedViaReflection
	public Game2048PerformanceMeasure(int sampleSize) {
		this(sampleSize, x -> {});
	}

	@Override
	public Game2048Measurement measure(RandomFactory<Agent<State2048, Action2048>> agentFactory, ThreadedContext context) {
		List<Game2048Outcome> result = context.invoke(
				(nothing, random) -> {
					Agent<State2048, Action2048> agent = agentFactory.create(random);
					Game2048Outcome game2048Outcome = game.playGame(agent, random.getRandomForThread());
					gamePlayedListener.accept(game2048Outcome);
					return game2048Outcome;
				},
				CollectionUtils.range(sampleSize)
		).toList();

		return new Game2048Measurement.Builder().addRaw(result).build();
	}
}
