package put.ci.cevo.experiments.dct.interaction;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.GenotypePhenotypeMapper;
import put.ci.cevo.games.dct.CAConfiguration;
import put.ci.cevo.games.dct.CADensity;

import static java.lang.Math.round;
import static put.ci.cevo.util.RandomUtils.shuffleInts;

public class CAConfigurationMapper implements GenotypePhenotypeMapper<CADensity, CAConfiguration> {

	@Override
	public CAConfiguration getPhenotype(CADensity genotype, RandomDataGenerator random) {
		long numOnes = round(genotype.getDensity() * genotype.getTestLength());
		int[] configuration = new int[genotype.getTestLength()];
		for (int i = 0; i < numOnes; i++) {
			configuration[i] = 1;
		}
		return new CAConfiguration(shuffleInts(configuration, random), genotype.getDensity());
	}

}
