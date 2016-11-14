package put.ci.cevo.experiments.dct.interaction;

import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.framework.IdentityGenotypePhenotypeMapper;
import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.interactions.InteractionResult;
import put.ci.cevo.games.dct.CADensity;
import put.ci.cevo.games.dct.CARule;

public class RuleDensityDCTInteraction implements InteractionDomain<CARule, CADensity> {

	private final StochasticDCTInteraction<CARule, CADensity> interaction;

	public RuleDensityDCTInteraction(int timeSteps, int radius) {
		this(new StochasticDCTInteraction<>(
			new IdentityGenotypePhenotypeMapper<CARule>(), new CAConfigurationMapper(), timeSteps, radius, 1));
	}

	public RuleDensityDCTInteraction(int timeSteps, int radius, int numSamples) {
		this(new StochasticDCTInteraction<>(
			new IdentityGenotypePhenotypeMapper<CARule>(), new CAConfigurationMapper(), timeSteps, radius, numSamples));
	}

	public RuleDensityDCTInteraction(StochasticDCTInteraction<CARule, CADensity> interaction) {
		this.interaction = interaction;
	}

	@Override
	public InteractionResult interact(CARule candidate, CADensity opponent, RandomDataGenerator random) {
		return interaction.interact(candidate, opponent, random);
	}

}
