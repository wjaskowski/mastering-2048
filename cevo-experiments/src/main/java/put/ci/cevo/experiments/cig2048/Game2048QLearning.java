package put.ci.cevo.experiments.cig2048;

import java.util.List;

import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.games.game2048.Action2048;
import put.ci.cevo.games.game2048.Game2048;
import put.ci.cevo.games.game2048.State2048;
import put.ci.cevo.rl.environment.AgentTransition;
import put.ci.cevo.util.Pair;
import put.ci.cevo.util.RandomUtils;

public class Game2048QLearning {

	private final Game2048 game;

	public Game2048QLearning() {
		game = new Game2048();
	}

	private double getBestValueAction(State2048 state, ActionValueFunction2048 qFunction) {
		List<Action2048> actions = game.getPossibleActions(state);

		double bestValue = Double.NEGATIVE_INFINITY;
		for (Action2048 action : actions) {
			double value = qFunction.getValue(state, action);
			bestValue = Math.max(value, bestValue);
		}

		return bestValue;
	}

	private AgentTransition<State2048, Action2048> chooseBestTransition(State2048 state, ActionValueFunction2048 qFunction) {
		List<Action2048> actions = game.getPossibleActions(state);

		double bestValue = Double.NEGATIVE_INFINITY;
		Action2048 bestAction = null;

		for (Action2048 action : actions) {
			double value = qFunction.getValue(state, action);
			if (value > bestValue) {
				bestAction = action;
				bestValue = value;
			}
		}

		return game.getAgentTransition(state, bestAction);
	}

	public Pair<Integer, Integer> play(ActionValueFunction2048 qFunction, RandomDataGenerator random) {
		int sumRewards = 0;

		State2048 state = game.sampleInitialStateDistribution(random);
		while (!game.isTerminal(state)) {
			AgentTransition<State2048, Action2048> agentTransition = chooseBestTransition(state, qFunction);
			sumRewards += agentTransition.getReward();
			state = game.getEnvironmentTransition(agentTransition.getAfterState(), random).getNextState();
		}

		return new Pair<>(sumRewards, state.getMaxTile());
	}

	public void QLearn(ActionValueFunction2048 qFunction, double explorationRate, double learningRate, RandomDataGenerator random) {
		Game2048 game = new Game2048();
		State2048 state = game.sampleInitialStateDistribution(random);

		while (!game.isTerminal(state)) {
			List<Action2048> actions = game.getPossibleActions(state);

			AgentTransition<State2048, Action2048> agentTransition = null;
			if (random.nextUniform(0, 1) < explorationRate) {
				Action2048 randomAction = RandomUtils.pickRandom(actions, random);
				agentTransition = game.getAgentTransition(state, randomAction);
			} else {
				agentTransition = chooseBestTransition(state, qFunction);
			}

			State2048 nextState = game.getEnvironmentTransition(agentTransition.getAfterState(), random).getNextState();

			double correctActionValue = agentTransition.getReward();
			if (!game.isTerminal(nextState)) {
				correctActionValue += getBestValueAction(nextState, qFunction);
			}

			qFunction.update(state, agentTransition.getAction(), correctActionValue, learningRate);
			state = nextState;
		}
	}
}
