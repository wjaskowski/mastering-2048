package put.ci.cevo.experiments.ntuple;

import static put.ci.cevo.util.ArrayUtils.toDoubleArray;
import static put.ci.cevo.util.ArrayUtils.toFloatArray;

import java.util.Arrays;

import put.ci.cevo.framework.operators.IndividualAdapter;
import put.ci.cevo.games.encodings.ntuple.NTuple;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.games.encodings.ntuple.NTuples.Builder;
import put.ci.cevo.util.vectors.DoubleVector;

public final class NTuplesDoubleVectorAdapter implements IndividualAdapter<NTuples, DoubleVector> {

	@Override
	public DoubleVector from(NTuples ntuples) {
		return new DoubleVector(toDoubleArray(ntuples.weights()));
	}

	@Override
	public NTuples from(DoubleVector vector, NTuples template) {
		Builder builder = new NTuples.Builder(template.getSymmetryExpander());
		float[] array = toFloatArray(vector.toArray());
		int idx = 0;
		for (NTuple original : template.getMain()) {
			// Split the weight array into subarrays of length equal to the length of the weight vector in the original
			// tuple
			float[] subarray = Arrays.copyOfRange(array, idx, idx + original.getNumWeights());
			builder.add(new NTuple(original.getNumValues(), original.getLocations(), subarray));
			idx += original.getNumWeights();
		}
		return builder.build();
	}
}
