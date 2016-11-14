package put.ci.cevo.experiments.ipd;

import static com.google.common.base.Objects.toStringHelper;
import static put.ci.cevo.util.MathUtils.isPerfectSquare;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.GenotypePhenotypeMapper;
import put.ci.cevo.games.ipd.IPDPlayer;

import com.google.common.base.Preconditions;

/**
 * For a memory-one n-choice IPD game a strategy representation is a direct look-up table which takes the form of
 * <code>n * n + 1</code> integer vector. The first move is specified independently as a first element of the vector.
 * 
 * This mapper transforms such an integer vector into a look-up table compatible with {@link IPDPlayer}.
 */
public class IPDLookupTableMapper implements GenotypePhenotypeMapper<IPDVector, IPDPlayer> {

	@Override
	public IPDPlayer getPhenotype(IPDVector genotype, RandomDataGenerator random) {
		Preconditions.checkArgument(isPerfectSquare(genotype.getVector().length - 1),
			"Invalid strategy vector length, unable to reshape it to a square matrix!");
		return new IPDPlayer(genotype.getVector());
	}

	@Override
	public String toString() {
		return toStringHelper(this).toString();
	}

}
