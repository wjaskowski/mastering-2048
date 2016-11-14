package put.ci.cevo.games.connect4.players;

import java.util.List;
import java.util.function.UnaryOperator;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.games.connect4.Connect4Action;
import put.ci.cevo.games.connect4.Connect4State;
import put.ci.cevo.games.connect4.evaluators.Connect4NTuplesQFunction;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.rl.agent.Agent;
import put.ci.cevo.rl.agent.Decision;
import put.ci.cevo.rl.agent.policies.GreedyQFunctionPolicy;
import put.ci.cevo.rl.agent.policies.QFunctionControlPolicy;
import put.ci.cevo.rl.evaluation.ActionValueFunction;

public class Connect4DeltaNTuplesAgent implements Agent<Connect4State, Connect4Action> {

	private final QFunctionControlPolicy<Connect4State, Connect4Action> policy = new GreedyQFunctionPolicy<>();
	private final ActionValueFunction<Connect4State, Connect4Action> qFunction;

	public Connect4DeltaNTuplesAgent(NTuples ntuples) {
		this(ntuples, x -> x);
	}


	public Connect4DeltaNTuplesAgent(NTuples ntuples,
			UnaryOperator<ActionValueFunction<Connect4State, Connect4Action>> qFunctionDecorator) {

		this.qFunction = qFunctionDecorator.apply(new Connect4NTuplesQFunction(ntuples));
	}

	@Override
	public Decision<Connect4Action> chooseAction(Connect4State state, List<Connect4Action> actions,
			RandomDataGenerator random) {
		return policy.chooseAction(state, actions, qFunction, random);
	}
}
