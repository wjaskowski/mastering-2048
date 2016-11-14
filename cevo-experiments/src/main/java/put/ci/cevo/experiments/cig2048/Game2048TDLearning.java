package put.ci.cevo.experiments.cig2048;

import java.util.List;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.games.game2048.Action2048;
import put.ci.cevo.games.game2048.Game2048;
import put.ci.cevo.games.game2048.Game2048Outcome;
import put.ci.cevo.games.game2048.State2048;
import put.ci.cevo.rl.agent.functions.RealFunction;
import put.ci.cevo.rl.environment.AgentTransition;
import put.ci.cevo.rl.environment.EnvTransition;
import put.ci.cevo.util.Pair;
import put.ci.cevo.util.RandomUtils;

public class Game2048TDLearning {

	private final Game2048 game;

	public Game2048TDLearning() {
		game = new Game2048();
	}

	private double getBestValueAction(State2048 state, RealFunction function) {
		List<Action2048> actions = game.getPossibleActions(state);

		double bestValue = Double.NEGATIVE_INFINITY;
		for (Action2048 action : actions) {
			AgentTransition<State2048, Action2048> agentTransition = game.getAgentTransition(state, action);
			double value = agentTransition.getReward() + function.getValue(agentTransition.getAfterState().getFeatures());
			if (value > bestValue) {
				bestValue = value;
			}
		}

		return bestValue;
	}

	private AgentTransition<State2048, Action2048> chooseBestTransition(State2048 state, RealFunction function) {
		List<Action2048> actions = game.getPossibleActions(state);
		AgentTransition<State2048, Action2048> bestAgentTransition = null;
		double bestValue = Double.NEGATIVE_INFINITY;

		for (Action2048 action : actions) {
			AgentTransition<State2048, Action2048> agentTransition = game.getAgentTransition(state, action);
			double value = agentTransition.getReward() + function.getValue(agentTransition.getAfterState().getFeatures());
			if (value > bestValue) {
				bestAgentTransition = agentTransition;
				bestValue = value;
			}
		}

		return bestAgentTransition;
	}

	private AgentTransition<State2048, Action2048> chooseBestTransitionExpectiMax(State2048 state, RealFunction function) {
		List<Action2048> actions = game.getPossibleActions(state);
		AgentTransition<State2048, Action2048> bestAgentTransition = null;
		double bestValue = Double.NEGATIVE_INFINITY;

		for (Action2048 action : actions) {
			AgentTransition<State2048, Action2048> agentTransition = game.getAgentTransition(state, action);

			double value = agentTransition.getReward();
			List<Pair<Double, EnvTransition<State2048>>> distribution = game
					.getEnvironmentTransitionDistribution(agentTransition.getAfterState());
			for (Pair<Double, EnvTransition<State2048>> transition : distribution) {
				value += (transition.first() * function.getValue(transition.second().getNextState().getFeatures()));
			}

			if (value > bestValue) {
				bestAgentTransition = agentTransition;
				bestValue = value;
			}
		}

		return bestAgentTransition;
	}

	public Game2048Outcome playByExpectimax(RealFunction vFunction, RandomDataGenerator random) {
		int sumRewards = 0;

		State2048 state = game.sampleInitialStateDistribution(random);
		while (!game.isTerminal(state)) {
			AgentTransition<State2048, Action2048> agentTransition = chooseBestTransitionExpectiMax(state, vFunction);
			sumRewards += agentTransition.getReward();
			state = game.getEnvironmentTransition(agentTransition.getAfterState(), random).getNextState();
		}

		return new Game2048Outcome(sumRewards, state);
	}

	public Game2048Outcome playByAfterstates(RealFunction vFunction, RandomDataGenerator random) {
		int sumRewards = 0;

		State2048 state = game.sampleInitialStateDistribution(random);
		while (!game.isTerminal(state)) {
			AgentTransition<State2048, Action2048> agentTransition = chooseBestTransition(state, vFunction);
			sumRewards += agentTransition.getReward();
			state = game.getEnvironmentTransition(agentTransition.getAfterState(), random).getNextState();
		}

		return new Game2048Outcome(sumRewards, state);
	}

	public void TDAfterstateLearn(NTuples vFunction, double explorationRate, double learningRate,
			RandomDataGenerator random) {
		Game2048 game = new Game2048(); // FIXME: This is hiding the global variable
		State2048 state = game.sampleInitialStateDistribution(random);

		while (!game.isTerminal(state)) {
			List<Action2048> actions = game.getPossibleActions(state);

			AgentTransition<State2048, Action2048> agentTransition;
			if (random.nextUniform(0, 1) < explorationRate) {
				Action2048 randomAction = RandomUtils.pickRandom(actions, random);
				agentTransition = game.getAgentTransition(state, randomAction);
			} else {
				agentTransition = chooseBestTransition(state, vFunction);
			}

			State2048 nextState = game.getEnvironmentTransition(agentTransition.getAfterState(), random).getNextState();

			double correctActionValue = 0;
			if (!game.isTerminal(nextState)) {
				correctActionValue += getBestValueAction(nextState, vFunction);
			}

			vFunction.update(agentTransition.getAfterState().getFeatures(), correctActionValue, learningRate);
			state = nextState;
		}
	}

	public void TDExpectimaxLearn(NTuples vFunction, double explorationRate, double learningRate,
			RandomDataGenerator random) {
		Game2048 game = new Game2048(); // FIXME: This is hiding the global variable
		State2048 state = game.sampleInitialStateDistribution(random);

		while (!game.isTerminal(state)) {
			List<Action2048> actions = game.getPossibleActions(state);

			AgentTransition<State2048, Action2048> agentTransition;
			if (random.nextUniform(0, 1) < explorationRate) {
				Action2048 randomAction = RandomUtils.pickRandom(actions, random);
				agentTransition = game.getAgentTransition(state, randomAction);
			} else {
				agentTransition = chooseBestTransitionExpectiMax(state, vFunction);
			}

			State2048 nextState = game.getEnvironmentTransition(agentTransition.getAfterState(), random).getNextState();

			double correctActionValue = agentTransition.getReward();
			if (!game.isTerminal(nextState)) {
				correctActionValue += vFunction.getValue(nextState.getFeatures());
			}

			vFunction.update(state.getFeatures(), correctActionValue, learningRate);
			state = nextState;
		}
	}

	public void TDLambdaLearn(NTuples vFunction, double explorationRate, double learningRate, double lambda,
			RandomDataGenerator random) {
		Game2048 game = new Game2048();
		State2048 state = game.sampleInitialStateDistribution(random);

//		int numTuples = vFunction.getAll().size();
//		Int2DoubleLinkedOpenHashMap[] eTraces = new Int2DoubleLinkedOpenHashMap[numTuples];
//		for (int i = 0; i < numTuples; i++) {
//			eTraces[i] = new Int2DoubleLinkedOpenHashMap();
//		}

		while (!game.isTerminal(state)) {
			List<Action2048> actions = game.getPossibleActions(state);

			Action2048 chosenAction = null;
			if (random.nextUniform(0, 1) < explorationRate) {
				chosenAction = RandomUtils.pickRandom(actions, random);
			} else {
				double bestValue = Double.NEGATIVE_INFINITY;
				for (Action2048 action : actions) {
					AgentTransition<State2048, Action2048> agentTransition = game.getAgentTransition(state, action
					);
					double value = vFunction.getValue(agentTransition.getAfterState().getFeatures());
					if (value > bestValue) { // resolve ties randomly?
						chosenAction = action;
						bestValue = value;
					}
				}
			}

			AgentTransition<State2048, Action2048> agentTransition = game.getAgentTransition(state, chosenAction
			);
			State2048 nextState = game.getEnvironmentTransition(agentTransition.getAfterState(), random).getNextState();

			double correctActionValue = agentTransition.getReward();
			if (!game.isTerminal(nextState)) {
				double value = vFunction.getValue(nextState.getFeatures());
				correctActionValue += value;
			}

			// double val = vFunction.getValue(state.getFeatures());
			// double error = correctActionValue - val;

			vFunction.update(state.getFeatures(), correctActionValue, learningRate);

//			for (int i = 0; i < numTuples; i++) {
//				Int2DoubleLinkedOpenHashMap map = eTraces[i];
//				for (int weight : map.keySet()) {
//					map.put(weight, map.get(weight) * lambda);
//				}
//			}
//
//			Game2048Board board = new Game2048Board(state.getFeatures());
//			for (int i = 0; i < numTuples; i++) {
//				NTuple tuple = vFunction.getTuple(i);
//				int weightIndex = tuple.address(board);
//
//				Int2DoubleLinkedOpenHashMap map = eTraces[i];
//				if (map.containsKey(weightIndex)) {
//					map.addTo(weightIndex, 1.0);
//				} else {
//					map.put(weightIndex, 1.0);
//				}
//
//				double[] weights = tuple.getWeights();
//				for (int weight : map.keySet()) {
//					weights[weight] += error * learningRate * map.get(weight);
//				}
//			}

			state = nextState;
		}
	}
}
