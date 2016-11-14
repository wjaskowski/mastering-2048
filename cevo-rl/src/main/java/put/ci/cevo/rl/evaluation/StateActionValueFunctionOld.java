package put.ci.cevo.rl.evaluation;

import put.ci.cevo.rl.agent.functions.RealFunction;
import put.ci.cevo.rl.environment.Action;
import put.ci.cevo.rl.environment.DirectEnvironmentEncoder;
import put.ci.cevo.rl.environment.Environment;
import put.ci.cevo.rl.environment.EnvironmentEncoder;
import put.ci.cevo.rl.environment.State;
import put.ci.cevo.rl.environment.AgentTransition;
import put.ci.cevo.util.annotations.AccessedViaReflection;

//TODO: This seems to be an AfterstateValueFunction, but why it gets state and action as an argument?


/**
 * @deprecated Use {@link LearnableActionValueFunction} instead
 */
@Deprecated
public class StateActionValueFunctionOld<S extends State, A extends Action> implements
		LearnableActionValueFunction<S, A> {

	private final RealFunction function;
	private final Environment<S, A> model;
	private final EnvironmentEncoder<S, A> encoder;

	@AccessedViaReflection
	public StateActionValueFunctionOld(RealFunction function, Environment<S, A> model) {
		this(function, model, new DirectEnvironmentEncoder<>());
	}

	@AccessedViaReflection
	public StateActionValueFunctionOld(RealFunction function, Environment<S, A> model, EnvironmentEncoder<S, A> encoder) {
		this.model = model;
		this.function = function;
		this.encoder = encoder;
	}

	@Override
	public double getValue(S state, A action) {
		AgentTransition<S, A> agentTransition = model.getAgentTransition(state, action);
		S afterState = agentTransition.getAfterState();
		return function.getValue(encoder.encode(afterState));
	}

	@Override
	public void update(S state, A action, double expectedValue, double learningRate) {
		function.update(encoder.encode(state), expectedValue, learningRate);
	}

}
