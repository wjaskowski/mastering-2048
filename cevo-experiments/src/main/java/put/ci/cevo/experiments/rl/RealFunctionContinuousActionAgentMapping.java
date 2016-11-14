package put.ci.cevo.experiments.rl;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.GenotypePhenotypeMapper;
import put.ci.cevo.rl.agent.Agent;
import put.ci.cevo.rl.agent.ContinuousActionAgent;
import put.ci.cevo.rl.agent.functions.RealFunction;
import put.ci.cevo.rl.environment.ContinuousAction;
import put.ci.cevo.rl.environment.EnvironmentEncoder;
import put.ci.cevo.rl.environment.State;
import put.ci.cevo.util.annotations.AccessedViaReflection;

public class RealFunctionContinuousActionAgentMapping<T extends RealFunction, S extends State> implements
		GenotypePhenotypeMapper<T, Agent<S, ContinuousAction>> {

	private EnvironmentEncoder<S, ContinuousAction> encoder;

	@AccessedViaReflection
	public RealFunctionContinuousActionAgentMapping() {
		this(null);
	}

	@AccessedViaReflection
	public RealFunctionContinuousActionAgentMapping(EnvironmentEncoder<S, ContinuousAction> encoder) {
		this.encoder = encoder;
	}

	@Override
	public Agent<S, ContinuousAction> getPhenotype(T function, RandomDataGenerator random) {
		return new ContinuousActionAgent<S>(function, encoder);
	}
}
