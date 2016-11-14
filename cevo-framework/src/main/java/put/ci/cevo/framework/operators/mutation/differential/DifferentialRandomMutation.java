package put.ci.cevo.framework.operators.mutation.differential;

import com.google.common.base.Preconditions;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.operators.crossover.CrossoverOperator;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.RandomUtils;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.vectors.DoubleVector;

import java.util.List;

import static java.util.Collections.singleton;
import static put.ci.cevo.util.sequence.Sequences.seq;

/**
 * Implements DE/rand/1 mutation. To be used with {@link CrossoverOperator}.
 */
public class DifferentialRandomMutation implements PopulationMutation<DoubleVector> {

	private final double scalingFactor;

	/**
	 * @param scalingFactor a.k.a. differential weight
	 */
	@AccessedViaReflection
	public DifferentialRandomMutation(double scalingFactor) {
		Preconditions.checkArgument(0 <= scalingFactor && scalingFactor <= 2, "In range [0,2] according to Wikipedia");
		this.scalingFactor = scalingFactor;
	}

	@Override
	public DoubleVector produce(DoubleVector individual, List<EvaluatedIndividual<DoubleVector>> population,
			RandomDataGenerator random) {
		List<DoubleVector> indivs = seq(population).map(EvaluatedIndividual.<DoubleVector> toIndividual()).toList();
		List<DoubleVector> sample = RandomUtils.sample(indivs, 3, singleton(individual), random);
		return createDonorVector(sample.get(0), sample.get(1), sample.get(2));
	}

	private DoubleVector createDonorVector(DoubleVector base, DoubleVector rand1, DoubleVector rand2) {
		double[] vector = base.toArray();
		for (int i = 0; i < vector.length; i++) {
			vector[i] += scalingFactor * (rand1.get(i) - rand2.get(i));
		}
		return new DoubleVector(vector);
	}

}