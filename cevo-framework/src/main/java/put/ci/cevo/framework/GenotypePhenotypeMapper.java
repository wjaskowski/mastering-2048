package put.ci.cevo.framework;

import org.apache.commons.math3.random.RandomDataGenerator;

//TODO: Why is that not an IndividualAdapter?
public interface GenotypePhenotypeMapper<G, P> {

	public P getPhenotype(G genotype, RandomDataGenerator random);

}
