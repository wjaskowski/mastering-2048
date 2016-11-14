package put.ci.cevo.rl.agent.policies;

import java.util.List;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.rl.agent.Decision;
import put.ci.cevo.rl.environment.Action;
import put.ci.cevo.rl.environment.State;
import put.ci.cevo.rl.evaluation.StateValueFunction;

/** Controls which action to make based on stateValueFunction and having access to the model */
public interface VFunctionControlPolicy<S extends State, A extends Action> {

	Decision<A> chooseAction(S state, List<A> actions, StateValueFunction<S> vFunction, RandomDataGenerator random);
}
