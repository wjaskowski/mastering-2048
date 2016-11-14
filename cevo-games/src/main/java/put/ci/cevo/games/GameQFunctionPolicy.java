package put.ci.cevo.games;

import java.util.List;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.games.board.Board;
import put.ci.cevo.rl.agent.Decision;
import put.ci.cevo.rl.agent.policies.EpsilonGreedyQFunctionPolicy;
import put.ci.cevo.rl.agent.policies.GreedyQFunctionPolicy;
import put.ci.cevo.rl.agent.policies.QFunctionControlPolicy;
import put.ci.cevo.rl.environment.Action;
import put.ci.cevo.rl.evaluation.ActionValueFunction;
import put.ci.cevo.util.annotations.AccessedViaReflection;

public class GameQFunctionPolicy<S extends GameState, A extends Action> implements QFunctionControlPolicy<S, A> {

	private QFunctionControlPolicy<S, A> innerPolicy;

	public GameQFunctionPolicy(double epsilon) {
		this.innerPolicy = new EpsilonGreedyQFunctionPolicy<>(new GreedyQFunctionPolicy<>(), epsilon);
	}

	@AccessedViaReflection
	public GameQFunctionPolicy(QFunctionControlPolicy<S, A> innerPolicy) {
		this.innerPolicy = innerPolicy;
	}

	private boolean isMaxPlayer(int currentPlayer) {
		return (currentPlayer == Board.BLACK);
	}

	@Override
	public Decision<A> chooseAction(S state, List<A> actions, ActionValueFunction<S, A> qFunction,
			RandomDataGenerator random) {
		return innerPolicy.chooseAction(state, actions, (s, a) -> {
			if (!isMaxPlayer(state.getPlayerToMove()))
				return -qFunction.getValue(s, a);
			else
				return qFunction.getValue(s, a);
		}, random);
	}
}