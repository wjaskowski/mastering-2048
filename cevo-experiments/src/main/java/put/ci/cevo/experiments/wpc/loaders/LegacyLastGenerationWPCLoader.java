package put.ci.cevo.experiments.wpc.loaders;

import put.ci.cevo.framework.individuals.loaders.DefaultIndividualsLoader;
import put.ci.cevo.framework.individuals.loaders.filters.LastGenerationIndividualsFilter;
import put.ci.cevo.rl.agent.functions.wpc.WPC;

public class LegacyLastGenerationWPCLoader extends DefaultIndividualsLoader<WPC> {

	public LegacyLastGenerationWPCLoader() {
		super(new ECJStatFileWPCLoader(), new LastGenerationIndividualsFilter<WPC>());
	}

}
