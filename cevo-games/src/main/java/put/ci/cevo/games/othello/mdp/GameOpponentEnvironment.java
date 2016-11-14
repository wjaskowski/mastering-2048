package put.ci.cevo.games.othello.mdp;

import java.util.List;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.games.GameState;
import put.ci.cevo.games.board.Board;
import put.ci.cevo.rl.agent.Agent;
import put.ci.cevo.rl.environment.*;

//TODO: I am not sure whether what it does it does well (where should be rewards? What rewards values when black,
// and what when white?). It might be impossible to create a general GameOpponentEnvironment so I make it deprecated
@Deprecated
public class GameOpponentEnvironment<S extends GameState, A extends Action> extends EnvironmentDecorator<S, A> {

	private Agent<S, A> opponent;
	private int opponentColor;

	public GameOpponentEnvironment(Environment<S, A> env, Agent<S, A> opponent, int opponentColor) {
		this.env = env;
		this.opponent = opponent;
		this.opponentColor = opponentColor;
	}

	@Override
	public S sampleInitialStateDistribution(RandomDataGenerator random) {
		S state = env.sampleInitialStateDistribution(random);
		if (opponentColor == Board.BLACK) {
			state = getEnvironmentTransition(state, random).getNextState();
		}
		return state;
	}

	@Override
	public EnvTransition<S> getEnvironmentTransition(S afterState, RandomDataGenerator random) {
		if (isTerminal(afterState)) {
			return new EnvTransition<>(afterState, 0, afterState);
		}

		List<A> possibleActions = getPossibleActions(afterState);

		A move = (possibleActions.size() == 0 ? null : opponent.chooseAction(afterState, possibleActions, random).getAction());
		AgentTransition<S, A> opponentTransition = getAgentTransition(afterState, move);
		return new EnvTransition<>(afterState, 0, opponentTransition.getAfterState());
	}

	@Override
	public double getAgentPerformance(double totalReward, int numSteps, S finalState) {
		if (opponentColor == Board.BLACK) {
			return -env.getAgentPerformance(totalReward, numSteps, finalState);
		} else {
			return env.getAgentPerformance(totalReward, numSteps, finalState);
		}
	}
}
