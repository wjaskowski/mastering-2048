package put.ci.cevo.rl.agent;

import java.util.List;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.rl.agent.policies.GreedyQFunctionPolicy;
import put.ci.cevo.rl.evaluation.ActionValueFunction;
import put.ci.cevo.rl.agent.policies.QFunctionControlPolicy;
import put.ci.cevo.rl.environment.Action;
import put.ci.cevo.rl.environment.State;
import put.ci.cevo.util.annotations.AccessedViaReflection;

public final class QFunctionAgent<S extends State, A extends Action> implements Agent<S, A> {

	private final QFunctionControlPolicy<S, A> policy;
	private final ActionValueFunction<S, A> actionValueFunction;

	@AccessedViaReflection
	public QFunctionAgent(ActionValueFunction<S, A> actionValueFunction) {
		this(actionValueFunction, new GreedyQFunctionPolicy<>());
	}

	@AccessedViaReflection
	public QFunctionAgent(ActionValueFunction<S, A> actionValueFunction, QFunctionControlPolicy<S, A> policy) {
		this.actionValueFunction = actionValueFunction;
		this.policy = policy;
	}

	@SuppressWarnings("unused")
	public ActionValueFunction<S, A> getActionValueFunction() {
		return actionValueFunction;
	}

	@SuppressWarnings("unused")
	public QFunctionControlPolicy<S, A> getPolicy() {
		return policy;
	}

	@Override
	public String toString() {
		return actionValueFunction.toString();
	}

	@Override
	public Decision<A> chooseAction(S state, List<A> availableActions, RandomDataGenerator random) {
		return policy.chooseAction(state, availableActions, actionValueFunction, random);
	}
}
