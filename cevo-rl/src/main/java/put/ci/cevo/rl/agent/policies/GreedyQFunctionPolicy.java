package put.ci.cevo.rl.agent.policies;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;
import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.rl.agent.Decision;
import put.ci.cevo.rl.environment.Action;
import put.ci.cevo.rl.environment.State;
import put.ci.cevo.rl.evaluation.ActionValueFunction;
import put.ci.cevo.util.RandomUtils;

public class GreedyQFunctionPolicy<S extends State, A extends Action> implements QFunctionControlPolicy<S, A> {

	public Decision<A> chooseAction(S state, List<A> actions, ActionValueFunction<S, A> qFunction,
			RandomDataGenerator random) {
		Preconditions.checkArgument(actions.size() > 0);

		double bestEval = Double.NEGATIVE_INFINITY;
		List<A> bestActions = new ArrayList<>();

		for (A action : actions) {
			double actionValue = qFunction.getValue(state, action);
			if (!bestActions.isEmpty() && actionValue == bestEval) {
				bestActions.add(action);
			} else if (bestActions.isEmpty() || actionValue > bestEval) {
				bestEval = actionValue;
				bestActions.clear();
				bestActions.add(action);
			}
		}

		return Decision.of(RandomUtils.pickRandom(bestActions, random), bestEval);
	}
}
