package put.ci.cevo.experiments.ntuple;

import put.ci.cevo.framework.operators.IndividualAdapter;
import put.ci.cevo.games.encodings.ntuple.DoubleNTuples;
import put.ci.cevo.util.vectors.DoubleVector;

public class DoubleNTuplesDoubleVectorAdapter implements IndividualAdapter<DoubleNTuples, DoubleVector> {
	final NTuplesDoubleVectorAdapter adapter = new NTuplesDoubleVectorAdapter();

	@Override
	public DoubleVector from(DoubleNTuples object) {
		return DoubleVector.concat(adapter.from(object.first()), adapter.from(object.second()));
	}

	@Override
	public DoubleNTuples from(DoubleVector object, DoubleNTuples template) {
		int n = template.first().totalWeights();
		int m = template.second().totalWeights();
		DoubleVector first = object.slice(0, n);
		DoubleVector second = object.slice(n, n + m);
		return new DoubleNTuples(adapter.from(first, template.first()), adapter.from(second, template.second()));
	}
}
