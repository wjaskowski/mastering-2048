package put.ci.cevo.rl.environment;

public class DirectEnvironmentEncoder<S extends State, A extends Action> implements EnvironmentEncoder<S, A> {

	@Override
	public double[] encode(S state) {
		return state.getFeatures();
	}

	@Override
	public double[] encode(A action) {
		return action.getDescription();
	}
}
