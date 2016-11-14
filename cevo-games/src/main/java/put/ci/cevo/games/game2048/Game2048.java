package put.ci.cevo.games.game2048;

import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.rl.agent.Agent;
import put.ci.cevo.rl.environment.AgentTransition;
import put.ci.cevo.rl.environment.EnvTransition;
import put.ci.cevo.rl.environment.Environment;
import put.ci.cevo.util.Pair;

public class Game2048 implements Environment<State2048, Action2048> {

	private final Function<RandomDataGenerator, State2048> initialStateGenerator;

	public Game2048() {
		this(State2048::getInitialState);
	}

	public Game2048(Function<RandomDataGenerator, State2048> initialStateGenerator) {
		this.initialStateGenerator = initialStateGenerator;
	}

	@Override
	public AgentTransition<State2048, Action2048> getAgentTransition(State2048 state, Action2048 action) {
		Preconditions.checkArgument(action != null);
		State2048 afterState = new State2048(state);
		int reward = afterState.makeMove(action);
		return new AgentTransition<>(state, action, reward, afterState);
	}

	@Override
	public EnvTransition<State2048> getEnvironmentTransition(State2048 afterState, RandomDataGenerator random) {
		State2048 nextState = new State2048(afterState);
		nextState.addRandomTile(random);
		return new EnvTransition<>(afterState, 0, nextState);
	}

	@Override
	public List<Action2048> getPossibleActions(State2048 state) {
		return state.getPossibleMoves();
	}

	@Override
	public State2048 sampleInitialStateDistribution(RandomDataGenerator random) {
		return initialStateGenerator.apply(random);
	}

	public List<Pair<Double, EnvTransition<State2048>>> getEnvironmentTransitionDistribution(
			State2048 afterState) {
		return afterState.getPossibleEnvironmentTransitions();
	}

	@Override
	public boolean isTerminal(State2048 state) {
		return state.isTerminal();
	}

	/** Like runEpisode, but returns also information about maxTile obtained */
	public Game2048Outcome playGame(Agent<State2048, Action2048> agent, RandomDataGenerator random) {
		final MutableObject<State2048> lastState = new MutableObject<>();

		//final MutableBoolean got2048 = new MutableBoolean(false);
		double sumRewards = runEpisode(agent, random, transition -> {
			State2048 state = transition.getNextState();
			/*if (got2048.isFalse() && state.getMaxTile() == 8192) {
				System.out.println(state.getBoard().toString("%2d "));
				got2048.setValue(true);
			}*/
			if (state.isTerminal())
				lastState.setValue(state);
		});

		return new Game2048Outcome((int)sumRewards, lastState.getValue());
	}

	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		RandomDataGenerator random = new RandomDataGenerator();

		Game2048 game = new Game2048();
		State2048 state = game.sampleInitialStateDistribution(random);
		while (!game.isTerminal(state)) {
			state.printHumanReadable();
			List<Action2048> actions = game.getPossibleActions(state);
			System.out.println("Possible actions : " + actions);

			while (true) {
				String line = in.nextLine();
				Action2048 action = null;
				if (line.toUpperCase().startsWith("W")) {
					action = Action2048.UP;
				}
				if (line.toUpperCase().startsWith("A")) {
					action = Action2048.LEFT;
				}
				if (line.toUpperCase().startsWith("S")) {
					action = Action2048.DOWN;
				}
				if (line.toUpperCase().startsWith("D")) {
					action = Action2048.RIGHT;
				}

				if (actions.contains(action)) {
					AgentTransition<State2048, Action2048> agentTransition = game.getAgentTransition(state, action
					);
					System.out.println("REWARD = " + agentTransition.getReward());
					state = game.getEnvironmentTransition(agentTransition.getAfterState(), random).getNextState();
					break;
				}
			}
		}
		in.close();
	}
}
