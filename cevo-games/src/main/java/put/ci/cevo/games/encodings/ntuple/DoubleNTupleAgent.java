package put.ci.cevo.games.encodings.ntuple;

import java.util.List;
import java.util.function.Function;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.games.TwoPlayerGameState;
import put.ci.cevo.rl.agent.Agent;
import put.ci.cevo.rl.agent.Decision;
import put.ci.cevo.rl.environment.Action;

/**
 * A generic agent operating on DoubleNTuples instead of (single) NTuples
 */
public class DoubleNTupleAgent<S extends TwoPlayerGameState, A extends Action> implements Agent<S, A> {
	private final Agent<S, A> firstAgent;
	private final Agent<S, A> secondAgent;

	public DoubleNTupleAgent(DoubleNTuples nTuples, Function<NTuples, Agent<S, A>> mapper) {
		firstAgent = mapper.apply(nTuples.first());
		secondAgent = mapper.apply(nTuples.second());
	}

	@Override
	public Decision<A> chooseAction(S state, List<A> availableActions, RandomDataGenerator random) {
		return state.isFirstPlayerToMove() ?
				firstAgent.chooseAction(state, availableActions, random) :
				secondAgent.chooseAction(state, availableActions, random);
	}
}
