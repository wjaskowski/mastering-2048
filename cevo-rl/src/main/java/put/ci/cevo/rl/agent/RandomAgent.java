package put.ci.cevo.rl.agent;

import java.util.List;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.rl.agent.policies.GreedyQFunctionPolicy;
import put.ci.cevo.rl.agent.policies.QFunctionControlPolicy;
import put.ci.cevo.rl.environment.Action;
import put.ci.cevo.rl.environment.State;
import put.ci.cevo.rl.evaluation.ActionValueFunction;
import put.ci.cevo.util.RandomUtils;
import put.ci.cevo.util.annotations.AccessedViaReflection;

public final class RandomAgent<S extends State, A extends Action> implements Agent<S, A> {

	@Override
	public Decision<A> chooseAction(S state, List<A> availableActions, RandomDataGenerator random) {
		return Decision.of(RandomUtils.pickRandom(availableActions, random));
	}
}
