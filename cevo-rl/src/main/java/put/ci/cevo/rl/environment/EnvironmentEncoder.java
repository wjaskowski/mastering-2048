package put.ci.cevo.rl.environment;

//TODO: Should be removed in favour of FeatureExtractor + something new for actions
public interface EnvironmentEncoder<S extends State, A extends Action> {

	public double[] encode(S state);

	public double[] encode(A action);
}
