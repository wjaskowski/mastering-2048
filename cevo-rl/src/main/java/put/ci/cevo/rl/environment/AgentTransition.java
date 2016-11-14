package put.ci.cevo.rl.environment;

public class AgentTransition<S extends State, A extends Action> {

	private S state;
	private A action;
	private S afterState;
	private double reward;

	public AgentTransition(S state, A action, double reward, S afterState) {
		this.state = state;
		this.action = action;
		this.afterState = afterState;
		this.reward = reward;
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

	public double getReward() {
		return reward;
	}
}
