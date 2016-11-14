package put.ci.cevo.experiments.dct.interaction;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.GenotypePhenotypeMapper;
import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.interactions.InteractionResult;
import put.ci.cevo.games.dct.CAConfiguration;
import put.ci.cevo.games.dct.CARule;
import put.ci.cevo.util.annotations.AccessedViaReflection;

import java.util.ArrayList;
import java.util.List;

public class StochasticDCTInteraction<S, T> implements InteractionDomain<S, T> {

	private final DCTInteraction domain;

	private final GenotypePhenotypeMapper<S, CARule> ruleMapper;
	private final GenotypePhenotypeMapper<T, CAConfiguration> testMapper;

	private final int numSamples;

	@AccessedViaReflection
	public StochasticDCTInteraction(GenotypePhenotypeMapper<S, CARule> ruleMapper,
			GenotypePhenotypeMapper<T, CAConfiguration> testMapper, int timeSteps, int radius, int numSamples) {
		this.domain = new DCTInteraction(timeSteps, radius);
		this.ruleMapper = ruleMapper;
		this.testMapper = testMapper;
		this.numSamples = numSamples;
	}

	@Override
	public InteractionResult interact(S solution, T test, RandomDataGenerator random) {
		List<InteractionResult> res = new ArrayList<>(numSamples);
		for (int i = 0; i < numSamples; i++) {
			res.add(domain.interact(ruleMapper.getPhenotype(solution, random), testMapper.getPhenotype(test, random),
					random));
		}
		return InteractionResult.aggregate(res);
	}

}
