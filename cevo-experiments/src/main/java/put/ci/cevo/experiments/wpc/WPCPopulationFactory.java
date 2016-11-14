package put.ci.cevo.experiments.wpc;

import put.ci.cevo.framework.factories.IndividualFactory;
import put.ci.cevo.framework.factories.UniformRandomPopulationFactory;
import put.ci.cevo.rl.agent.functions.wpc.WPC;
import put.ci.cevo.util.annotations.AccessedViaReflection;

public class WPCPopulationFactory extends UniformRandomPopulationFactory<WPC> {

	@AccessedViaReflection
	public WPCPopulationFactory(IndividualFactory<WPC> individualFactory) {
		super(individualFactory);
	}

	@AccessedViaReflection
	public WPCPopulationFactory(double maxWeight, double minWeight, int wpcLength) {
		this(new WPCIndividualFactory(wpcLength, minWeight, maxWeight));
	}

}
