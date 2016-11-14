package put.ci.cevo.experiments.wpc;

import java.util.List;

import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.framework.operators.mutation.differential.DifferentialRandomMutation;
import put.ci.cevo.framework.operators.mutation.differential.PopulationMutation;
import put.ci.cevo.framework.operators.mutation.differential.PopulationMutationAdapter;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.vectors.DoubleVector;
import put.ci.cevo.rl.agent.functions.wpc.WPC;
import put.ci.cevo.util.annotations.AccessedViaReflection;

public class WPCDifferentialRandomMutation implements PopulationMutation<WPC> {

	private final PopulationMutationAdapter<WPC, DoubleVector> mutation;

	@AccessedViaReflection
	public WPCDifferentialRandomMutation(double scalingFactor) {
		this.mutation = new PopulationMutationAdapter<>(
			new DifferentialRandomMutation(scalingFactor), new WPCDoubleVectorAdapter());
	}

	@Override
	public WPC produce(WPC individual, List<EvaluatedIndividual<WPC>> population, RandomDataGenerator random) {
		return mutation.produce(individual, population, random);
	}

}
