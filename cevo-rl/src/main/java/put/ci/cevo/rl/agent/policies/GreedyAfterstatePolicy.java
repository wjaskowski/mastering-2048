package put.ci.cevo.rl.agent.policies;

import java.util.List;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.rl.agent.Decision;
import put.ci.cevo.rl.environment.Action;
import put.ci.cevo.rl.environment.AgentTransition;
import put.ci.cevo.rl.environment.Environment;
import put.ci.cevo.rl.environment.State;
import put.ci.cevo.rl.evaluation.StateValueFunction;
import put.ci.cevo.util.annotations.AccessedViaReflection;

/**
 * Selects action that leads to an afterstate with highest
 *   i) value(afterstate) + reward(transition) if afterstate is not terminal
 *  ii) reward(transition) if afterstate is terminal
 */
@AccessedViaReflection
public class GreedyAfterstatePolicy<S extends State, A extends Action> implements VFunctionControlPolicy<S, A> {

	private static final boolean AVOID_TERMINAL_STATES_FALSE = false;

	// The implementation uses GreedyQFunctionPolicy
	private final GreedyQFunctionPolicy<S, A> greedyModelFreePolicy = new GreedyQFunctionPolicy<>();
	private final boolean avoidTerminalStates;
	private final Environment<S, A> model;

	@AccessedViaReflection
	public GreedyAfterstatePolicy(Environment<S, A> model) {
		this(model, AVOID_TERMINAL_STATES_FALSE);
	}

	public GreedyAfterstatePolicy(Environment<S, A> model, boolean avoidTerminalStates) {
		this.model = model;
		this.avoidTerminalStates = avoidTerminalStates;
	}

	@Override
	public Decision<A> chooseAction(S state, List<A> actions, StateValueFunction<S> afterStateFunction,
			RandomDataGenerator random) {

		return greedyModelFreePolicy.chooseAction(state, actions,
				// Q(state, action) = V(afterState) + reward, where: state  --action-->  afterstate
				(s, a) -> {
					AgentTransition<S, A> transition = model.getAgentTransition(s, a);
					if (model.isTerminal(transition.getAfterState())) {
						return avoidTerminalStates ? -Double.MAX_VALUE : transition.getReward();
					} else {
						return afterStateFunction.getValue(transition.getAfterState()) + transition.getReward();
					}
				}, random);
	}
}
