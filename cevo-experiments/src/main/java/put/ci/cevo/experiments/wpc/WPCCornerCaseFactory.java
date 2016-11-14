package put.ci.cevo.experiments.wpc;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.factories.IndividualFactory;
import put.ci.cevo.rl.agent.functions.wpc.WPC;

public class WPCCornerCaseFactory implements IndividualFactory<WPC> {

	private int wpcLength;

	public WPCCornerCaseFactory(int wpcLength) {
		this.wpcLength = wpcLength;
	}

	@Override
	public WPC createRandomIndividual(RandomDataGenerator random) {
		double[] wpc = new double[wpcLength];
		for (int i = 0; i < wpcLength; i++) {
			wpc[i] = random.nextGaussian(0, 1) > 0 ? 10 : -10;
		}
		return new WPC(wpc);
	}
}
