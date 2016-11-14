package put.ci.cevo.experiments.numbers;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.GenotypePhenotypeMapper;
import put.ci.cevo.games.number.NumbersGamePlayer;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.vectors.DoubleVector;

public class NumbersGameMapper implements GenotypePhenotypeMapper<DoubleVector, NumbersGamePlayer> {

	private final double discretization;

	@AccessedViaReflection
	public NumbersGameMapper(double discretization) {
		this.discretization = discretization;
	}

	@Override
	public NumbersGamePlayer getPhenotype(DoubleVector genotype, RandomDataGenerator random) {
		return new NumbersGamePlayer(genotype.toArray(), discretization);
	}

}
