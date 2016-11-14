package put.ci.cevo.experiments.rl;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.GenotypePhenotypeMapper;
import put.ci.cevo.rl.agent.Agent;
import put.ci.cevo.rl.agent.QFunctionAgent;
import put.ci.cevo.rl.agent.functions.RealActionValueFunction;
import put.ci.cevo.rl.agent.functions.RealFunction;
import put.ci.cevo.rl.evaluation.ActionValueFunction;
import put.ci.cevo.rl.agent.policies.QFunctionControlPolicy;
import put.ci.cevo.rl.agent.policies.GreedyQFunctionPolicy;
import put.ci.cevo.rl.environment.*;
import put.ci.cevo.util.annotations.AccessedViaReflection;

public class RealFunctionActionValueAgentMapping<T extends RealFunction, S extends State, A extends Action> implements
		GenotypePhenotypeMapper<T, Agent<S, A>> {

	private final QFunctionControlPolicy<S, A> policy;
	private EnvironmentEncoder<S, A> encoder;
	private Environment<S, A> env;

	@AccessedViaReflection
	public RealFunctionActionValueAgentMapping(Environment<S, A> env) {
		this(new GreedyQFunctionPolicy<S, A>(), null, env);
	}

	@AccessedViaReflection
	public RealFunctionActionValueAgentMapping(EnvironmentEncoder<S, A> encoder, Environment<S, A> env) {
		this(new GreedyQFunctionPolicy<S, A>(), encoder, env);
	}

	@AccessedViaReflection
	public RealFunctionActionValueAgentMapping(QFunctionControlPolicy<S, A> policy, EnvironmentEncoder<S, A> encoder, Environment<S, A> env) {
		this.policy = policy;
		this.encoder = encoder;
		this.env = env;
	}

	@Override
	public Agent<S, A> getPhenotype(T function, RandomDataGenerator random) {
		ActionValueFunction<S, A> actionValueFunction = new RealActionValueFunction<S, A>(
			function, encoder == null ? new DirectEnvironmentEncoder<S, A>() : encoder);
		return new QFunctionAgent<S, A>(actionValueFunction, policy);
	}
}
