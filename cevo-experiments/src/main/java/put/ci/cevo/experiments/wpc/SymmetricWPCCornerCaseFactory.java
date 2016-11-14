package put.ci.cevo.experiments.wpc;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.factories.IndividualFactory;
import put.ci.cevo.rl.agent.functions.wpc.Symmetric8x8WPC;
import put.ci.cevo.rl.agent.functions.wpc.WPC;

public class SymmetricWPCCornerCaseFactory implements IndividualFactory<WPC> {

	@Override
	public WPC createRandomIndividual(RandomDataGenerator random) {
		double[] weights = new double[Symmetric8x8WPC.NUM_WEIGHTS];
		for (int i = 0; i < Symmetric8x8WPC.NUM_WEIGHTS; i++) {
			weights[i] = random.nextGaussian(0, 1) > 0 ? 1 : -1;
		}
		Symmetric8x8WPC symmetricWPC = new Symmetric8x8WPC(weights);
		return symmetricWPC.getFullWPC();
	}
}
