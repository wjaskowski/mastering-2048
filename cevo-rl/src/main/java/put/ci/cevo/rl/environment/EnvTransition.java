package put.ci.cevo.rl.environment;

public class EnvTransition<S extends State> {

	private S nextState;
	private S afterState;
	private double reward;

	public EnvTransition(S afterState, double reward, S nextState) {
		this.nextState = nextState;
		this.afterState = afterState;
		this.reward = reward;
	}

	public S getNextState() {
		return nextState;
	}

	public S getAfterState() {
		return afterState;
	}

	public double getReward() {
		return reward;
	}
}
