package put.ci.cevo.experiments.wpc;

import put.ci.cevo.framework.operators.IndividualAdapter;
import put.ci.cevo.util.vectors.DoubleVector;
import put.ci.cevo.rl.agent.functions.wpc.Symmetric8x8WPC;

public final class SymmetricWPCDoubleVectorAdapter implements IndividualAdapter<Symmetric8x8WPC, DoubleVector> {

	@Override
	public DoubleVector from(Symmetric8x8WPC wpc) {
		return new DoubleVector(wpc.getWeights());
	}

	@Override
	public Symmetric8x8WPC from(DoubleVector vector, Symmetric8x8WPC template) {
		return new Symmetric8x8WPC(vector.toArray());
	}
}
