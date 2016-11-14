package put.ci.cevo.experiments.othello;

import put.ci.cevo.experiments.wpc.WPCPopulationFactory;
import put.ci.cevo.util.annotations.AccessedViaReflection;

public class OthelloWPCPopulationFactory extends WPCPopulationFactory {

	@AccessedViaReflection
	public OthelloWPCPopulationFactory() {
		super(new OthelloWPCIndividualFactory());
	}

}
