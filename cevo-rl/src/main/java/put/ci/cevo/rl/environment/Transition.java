package put.ci.cevo.rl.environment;

import com.google.common.base.Preconditions;

public class Transition<S extends State, A extends Action> {
	private S state;
	private A action;
	private S nextState;
	private S afterState;
	private double reward;

	public Transition(S state, A action, S afterState, double reward, S nextState) {
		this.state = state;
		this.action = action;
		this.afterState = afterState;
		this.nextState = nextState;
		this.reward = reward;
	}

	public Transition(AgentTransition<S, A> agentTransition, EnvTransition<S> envTransition) {
		this(agentTransition.getState(), agentTransition.getAction(), agentTransition.getAfterState(),
				agentTransition.getReward() + envTransition.getReward(), envTransition.getNextState());
		Preconditions.checkArgument(agentTransition.getAfterState() == envTransition.getAfterState());
	}

	public S getState() {
		return state;
	}

	public A getAction() {
		return action;
	}

	public S getAfterState() {
		return afterState;
	}

	public S getNextState() {
		return nextState;
	}

	public double getReward() {
		return reward;
	}
}
