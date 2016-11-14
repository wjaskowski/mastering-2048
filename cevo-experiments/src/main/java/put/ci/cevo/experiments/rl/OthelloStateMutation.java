package put.ci.cevo.experiments.rl;

import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.framework.operators.mutation.MutationOperator;
import put.ci.cevo.games.othello.mdp.OthelloSelfPlayEnvironment;
import put.ci.cevo.games.othello.mdp.OthelloState;
import put.ci.cevo.util.annotations.AccessedViaReflection;

public class OthelloStateMutation implements MutationOperator<OthelloState> {

	private final StateIndividualFactory<OthelloState> stateFactory;
	private double newStateProbability;
	private int mutatingDepth;

	@AccessedViaReflection
	public OthelloStateMutation(int mutatingDepth, int minNumMovesToEnd, double newStateProbability) {
		this.mutatingDepth = mutatingDepth;
		this.newStateProbability = newStateProbability;
		stateFactory = new StateIndividualFactory<>(new OthelloSelfPlayEnvironment(), minNumMovesToEnd);
	}

	@Override
	public OthelloState produce(OthelloState individual, RandomDataGenerator random) {
		if (random.nextUniform(0, 1) < newStateProbability) {
			return stateFactory.createRandomIndividual(random);
		} else {
			int stateDepth = individual.getDepth();
			if (mutatingDepth > 0) {
				stateDepth += random.nextInt(-mutatingDepth, mutatingDepth);
			}
			
			return stateFactory.createRandomIndividualAtDepth(Math.max(0, stateDepth), random);
		}
	}

}
