package put.ci.cevo.rl.agent;

import java.util.List;

import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.rl.agent.functions.RealFunction;
import put.ci.cevo.rl.environment.ContinuousAction;
import put.ci.cevo.rl.environment.EnvironmentEncoder;
import put.ci.cevo.rl.environment.State;

@Deprecated
/**
 * @deprecated Will be removed/changed in the future because of the encoder (=> FeaturesExtractor)
 */
public class ContinuousActionAgent<S extends State> implements Agent<S, ContinuousAction> {

	private RealFunction function;
	private EnvironmentEncoder<S, ContinuousAction> encoder;

	public ContinuousActionAgent(RealFunction function, EnvironmentEncoder<S, ContinuousAction> encoder) {
		this.function = function;
		this.encoder = encoder;
	}

	@Override
	public Decision<ContinuousAction> chooseAction(S state, List<ContinuousAction> availableActions,
			RandomDataGenerator random) {
		return Decision.of(new ContinuousAction(function.getValue(encoder.encode(state))));
	}
}
