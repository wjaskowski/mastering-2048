package put.ci.cevo.rl.agent.policies;

import java.util.List;

import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.rl.agent.Decision;
import put.ci.cevo.rl.environment.Action;
import put.ci.cevo.rl.environment.State;
import put.ci.cevo.rl.evaluation.ActionValueFunction;

public interface QFunctionControlPolicy<S extends State, A extends Action> {

	Decision<A> chooseAction(S state, List<A> actions, ActionValueFunction<S, A> qFunction, RandomDataGenerator random);
}
