package put.ci.cevo.experiments.wpc;

import put.ci.cevo.framework.operators.IndividualAdapter;
import put.ci.cevo.util.vectors.DoubleVector;
import put.ci.cevo.rl.agent.functions.wpc.WPC;

public final class WPCDoubleVectorAdapter implements IndividualAdapter<WPC, DoubleVector> {

	@Override
	public DoubleVector from(WPC wpc) {
		return new DoubleVector(wpc.getWeights());
	}

	@Override
	public WPC from(DoubleVector vector, WPC template) {
		return new WPC(vector.toArray());
	}
}
