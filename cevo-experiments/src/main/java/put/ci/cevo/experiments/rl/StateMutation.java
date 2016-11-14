package put.ci.cevo.experiments.rl;

import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.framework.operators.mutation.MutationOperator;
import put.ci.cevo.rl.environment.Environment;
import put.ci.cevo.rl.environment.State;
import put.ci.cevo.rl.environment.AgentTransition;
import put.ci.cevo.rl.environment.StateTrajectory;
import put.ci.cevo.util.annotations.AccessedViaReflection;

public class StateMutation<S extends State> implements MutationOperator<S> {

	private Environment<S, ?> environment;
	private StateIndividualFactory<S> factory;
	private int numMutatingMoves;

	@AccessedViaReflection
	public StateMutation(Environment<S, ?> environment, StateIndividualFactory<S> factory, int numMutatingMoves) {
		this.environment = environment;
		this.factory = factory;
		this.numMutatingMoves = numMutatingMoves;
	}

	@Override
	public S produce(S individual, RandomDataGenerator random) {
		S state = individual;
		for (int i = 0; i < numMutatingMoves; i++) {
			if (environment.isTerminal(state)) {
				return factory.createRandomIndividual(random);
			}

			AgentTransition<S, ?> agentTransition = StateTrajectory.getRandomTransition(environment, state,
					random);
			state = agentTransition.getAfterState();
		}

		return state;
	}

}
