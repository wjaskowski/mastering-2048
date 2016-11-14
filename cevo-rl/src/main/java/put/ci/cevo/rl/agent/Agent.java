package put.ci.cevo.rl.agent;

import java.util.List;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.rl.environment.Action;
import put.ci.cevo.rl.environment.State;

public interface Agent<S extends State, A extends Action> {

	Decision<A> chooseAction(S state, List<A> availableActions, RandomDataGenerator random);
}
