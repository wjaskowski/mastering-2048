package put.ci.cevo.experiments.ipd;

import put.ci.cevo.framework.factories.IndividualFactory;
import put.ci.cevo.framework.factories.UniformRandomPopulationFactory;
import put.ci.cevo.util.annotations.AccessedViaReflection;

public class IPDPopulationFactory extends UniformRandomPopulationFactory<IPDVector> {

	@AccessedViaReflection
	public IPDPopulationFactory(IndividualFactory<IPDVector> individualFactory) {
		super(individualFactory);
	}

	@AccessedViaReflection
	public IPDPopulationFactory(int choices) {
		super(new IPDVectorIndividualFactory(choices));
	}
}
