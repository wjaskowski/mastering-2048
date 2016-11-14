package put.ci.cevo.games.game2048;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.carrotsearch.hppc.LongDoubleOpenHashMap;
import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.rl.agent.Decision;
import put.ci.cevo.rl.agent.policies.VFunctionControlPolicy;
import put.ci.cevo.rl.environment.AgentTransition;
import put.ci.cevo.rl.environment.EnvTransition;
import put.ci.cevo.rl.evaluation.StateValueFunction;
import put.ci.cevo.util.Deadline;
import put.ci.cevo.util.Pair;
import put.ci.cevo.util.RandomUtils;
import put.ci.cevo.util.annotations.AccessedViaReflection;

/**
 * Uses expectimax algorithm with a given depth to choose the action leading to the largest afterstate value (taking
 * into account also the rewards)
 */
//TODO: Could be made general for KnownEnvironment if it has getNextStatesDistribution().
@AccessedViaReflection
public class Game2048ExpectimaxIterativeDeepeningPolicy implements VFunctionControlPolicy<State2048, Action2048> {

	private final Game2048 model;
	private final int startDepth;
	private final int maxMillisecondsPerMove;

	@AccessedViaReflection
	public Game2048ExpectimaxIterativeDeepeningPolicy(Game2048 model, int startDepth, int maxMillisecondsPerMove) {
		Preconditions.checkArgument(startDepth >= 1);
		this.model = model;
		this.startDepth = startDepth;
		this.maxMillisecondsPerMove = maxMillisecondsPerMove;
	}

	@Override
	public Decision<Action2048> chooseAction(State2048 state, List<Action2048> actions,
			StateValueFunction<State2048> vFunction, RandomDataGenerator random) {
		assert !state.isTerminal();

		Deadline deadline = new Deadline(maxMillisecondsPerMove);
		Stopwatch sw = Stopwatch.createStarted();
		Decision<Action2048> decision = chooseAction(state, actions, vFunction, random, startDepth, Deadline.NoDeadline());
		double lastDeepeningTime = sw.elapsed(TimeUnit.MICROSECONDS) / 1000.0;
		int actualDepth = startDepth;
		for (int depth = startDepth + 1; ; depth++) {
			if (deadline.getTimeToExpireMilliSeconds() < lastDeepeningTime * 2)
				break;
			sw.reset(); sw.start();
			Decision<Action2048> deeperDecision = chooseAction(state, actions, vFunction, random, depth, deadline);
			lastDeepeningTime = sw.elapsed(TimeUnit.MICROSECONDS) / 1000.0;
			if (deadline.hasExpired())
				break;
			decision = deeperDecision;
			actualDepth = depth;
		}
		//System.out.println("actualDepth: " + actualDepth);
		return decision;
	}

	public Decision<Action2048> chooseAction(State2048 state, List<Action2048> actions,
			StateValueFunction<State2048> vFunction, RandomDataGenerator random, int maxDepth, Deadline deadline) {

		double bestValue = Double.NEGATIVE_INFINITY;
		Action2048 bestAction = null;

		LongDoubleOpenHashMap afterstateCache = new LongDoubleOpenHashMap(1000);

		//System.out.println(state.getBoard().toString("%2d "));
		for (Action2048 action : RandomUtils.shuffle(actions, random)) {
			AgentTransition<State2048, Action2048> agentTransition = model.getAgentTransition(state, action);
			double value = deepAfterstateValue(agentTransition.getAfterState(), vFunction, afterstateCache,
					maxDepth - 1, deadline) + agentTransition.getReward();
			//System.out.println(String.format("%8s: %6.0f (1-ply: %6.0f)", action.toString(), value, vFunction.getValue(
			//		agentTransition.getAfterState()) + agentTransition.getReward()));
			if (bestValue < value) {
				bestValue = value;
				bestAction = action;
			}
		}
		//System.out.println();

		return Decision.of(bestAction, bestValue);
	}

	private double deepAfterstateValue(State2048 afterstate, StateValueFunction<State2048> vFunction,
			LongDoubleOpenHashMap afterstateCache, int depth, Deadline deadline) {

		long board = afterstate.getBoard().asLong();
		double cacheValue = afterstateCache.getOrDefault(board, Double.NEGATIVE_INFINITY);
		if (cacheValue != Double.NEGATIVE_INFINITY) {
			return cacheValue;
		}

		double expectedValue = 0.0;
		if (depth == 0) {
			expectedValue = vFunction.getValue(afterstate);
		} else {
			for (Pair<Double, EnvTransition<State2048>> entry : model.getEnvironmentTransitionDistribution(
					afterstate)) {
				if (deadline.hasExpired())
					break;
				double probability = entry.first();
				EnvTransition<State2048> transition = entry.second();
				expectedValue += probability * (deepStateValue(transition.getNextState(), vFunction, afterstateCache,
						depth, deadline) + transition.getReward());
			}
		}

		afterstateCache.put(board, expectedValue);

		return expectedValue;
	}

	private double deepStateValue(State2048 state, StateValueFunction<State2048> vFunction,
			LongDoubleOpenHashMap afterstateCache, int depth, Deadline deadline) {
		if (model.isTerminal(state)) {
			return 0;
		}

		double bestValue = Double.NEGATIVE_INFINITY;
		for (Action2048 action : model.getPossibleActions(state)) {
			AgentTransition<State2048, Action2048> agentTransition = model.getAgentTransition(state, action);
			double value = deepAfterstateValue(agentTransition.getAfterState(), vFunction, afterstateCache,
					depth - 1, deadline) + agentTransition.getReward();
			if (bestValue < value) {
				bestValue = value;
			}
		}
		return bestValue;
	}
}
