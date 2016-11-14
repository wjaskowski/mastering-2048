package put.ci.cevo.games.tetris.agents;

import java.util.List;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.games.tetris.TetrisAction;
import put.ci.cevo.games.tetris.TetrisState;
import put.ci.cevo.rl.agent.Agent;
import put.ci.cevo.rl.agent.Decision;
import put.ci.cevo.rl.agent.QFunctionAgent;

public class DeltaNTuplesTetrisAgent implements Agent<TetrisState, TetrisAction> {

	private final QFunctionAgent<TetrisState, TetrisAction> agent;

	public DeltaNTuplesTetrisAgent(NTuples ntuples) {
		// It is actually an afterstate value function, but implemented as Q-Function
		this.agent = new QFunctionAgent<>(new DeltaNTuplesTetrisActionValueFunction(ntuples, true));
	}

	@Override
	public Decision<TetrisAction> chooseAction(TetrisState state, List<TetrisAction> availableActions,
			RandomDataGenerator random) {
		return agent.chooseAction(state, availableActions, random);
	}
}
