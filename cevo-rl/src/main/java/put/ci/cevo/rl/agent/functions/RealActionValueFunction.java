package put.ci.cevo.rl.agent.functions;

import org.apache.commons.lang.ArrayUtils;

import put.ci.cevo.rl.environment.Action;
import put.ci.cevo.rl.environment.DirectEnvironmentEncoder;
import put.ci.cevo.rl.environment.EnvironmentEncoder;
import put.ci.cevo.rl.environment.State;
import put.ci.cevo.rl.evaluation.LearnableActionValueFunction;

/**
 * TODO: Get rid of encoders. FeaturesExtractors
 */
public class RealActionValueFunction<S extends State, A extends Action> implements LearnableActionValueFunction<S, A> {

	private final RealFunction function;
	private final EnvironmentEncoder<S, A> encoder;

	public RealActionValueFunction(RealFunction function) {
		this(function, new DirectEnvironmentEncoder<S, A>());
	}

	public RealActionValueFunction(RealFunction function, EnvironmentEncoder<S, A> encoder) {
		this.function = function;
		this.encoder = encoder;
	}

	@Override
	public double getValue(S state, A action) {
		double[] input = ArrayUtils.addAll(encoder.encode(state), encoder.encode(action));
		return function.getValue(input);
	}

	@Override
	public void update(S state, A action, double expectedValue, double learningRate) {
		double[] input = ArrayUtils.addAll(encoder.encode(state), encoder.encode(action));
		function.update(input, expectedValue, learningRate);
	}
}
