package put.ci.cevo.framework;

import org.apache.commons.math3.random.RandomDataGenerator;

public class IdentityGenotypePhenotypeMapper<T> implements GenotypePhenotypeMapper<T, T> {

	@Override
	public T getPhenotype(T genotype, RandomDataGenerator random) {
		return genotype;
	}

}
