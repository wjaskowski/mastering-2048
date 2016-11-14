package put.ci.cevo.experiments.mlp;

import put.ci.cevo.framework.operators.IndividualAdapter;
import put.ci.cevo.util.vectors.DoubleVector;
import put.ci.cevo.rl.agent.functions.mlp.MLP;

public class MLPDoubleVectorAdapter implements IndividualAdapter<MLP, DoubleVector> {

	@Override
	public DoubleVector from(MLP mlp) {
		return new DoubleVector(mlp.getWeights());
	}

	@Override
	public MLP from(DoubleVector vector, MLP template) {
		return MLP.fromWeights(vector.toArray(), template);
	}
}
