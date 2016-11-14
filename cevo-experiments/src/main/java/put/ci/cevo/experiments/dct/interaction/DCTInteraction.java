package put.ci.cevo.experiments.dct.interaction;

import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.interactions.InteractionResult;
import put.ci.cevo.games.GameOutcome;
import put.ci.cevo.games.dct.CAConfiguration;
import put.ci.cevo.games.dct.CARule;
import put.ci.cevo.games.dct.DensityClassificationTask;
import put.ci.cevo.util.annotations.AccessedViaReflection;

public class DCTInteraction implements InteractionDomain<CARule, CAConfiguration> {

	private final int timeSteps;
	private final int radius;

	@AccessedViaReflection
	public DCTInteraction(int timeSteps, int radius) {
		this.timeSteps = timeSteps;
		this.radius = radius;
	}

	@Override
	public InteractionResult interact(CARule rule, CAConfiguration test, RandomDataGenerator random) {
		DensityClassificationTask dct = new DensityClassificationTask(timeSteps, radius);
		GameOutcome outcome = dct.play(rule, test, random);
		return new InteractionResult(outcome.playerPoints(), outcome.opponentPoints(), 1);
	}

}
