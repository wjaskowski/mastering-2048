package put.ci.cevo.experiments.ipd;

import static put.ci.cevo.util.sequence.Sequences.seq;

import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;

import put.ci.cevo.util.vectors.IntegerVector;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.sequence.Sequence;
import put.ci.cevo.util.sequence.transforms.Transform;

/**
 * Generates all possible IPD strategies in a {@link IntegerVector} representation of given length and for a range of
 * integers described by min and max. Note that the number of generated strategies grows exponentially with the formula
 * n^k, where n = |max - min| and k = length.
 */
public class IPDStrategiesGenerator {

	private final int choices;

	@AccessedViaReflection
	public IPDStrategiesGenerator(int choices) {
		this.choices = choices;
	}

	/**
	 * The returned {@link Sequence} is not materialised in the operating memory. It can be viewed as a infinite stream
	 * of data. Be careful not to materialise this sequences as it will most likely cause {@link OutOfMemoryError}.
	 */
	public Sequence<IntegerVector> createStrategies() {
		ICombinatoricsVector<Integer> vector = Factory.range(0, choices - 1);
		Generator<Integer> generator = Factory.createPermutationWithRepetitionGenerator(vector, choices * choices + 1);
		return seq(generator).map(new Transform<ICombinatoricsVector<Integer>, IntegerVector>() {
			@Override
			public IntegerVector transform(ICombinatoricsVector<Integer> permutation) {
				return new IntegerVector(permutation.getVector());
			}
		});
	}

	public static Sequence<IntegerVector> createStrategies(int choices) {
		return new IPDStrategiesGenerator(choices).createStrategies();
	}

}
