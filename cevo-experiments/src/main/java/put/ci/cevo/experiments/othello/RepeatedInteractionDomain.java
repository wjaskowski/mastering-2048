package put.ci.cevo.experiments.othello;

import com.google.common.base.Preconditions;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.interactions.InteractionResult;
import put.ci.cevo.util.annotations.AccessedViaReflection;

/**
 * Repeats the interaction a number of times summing the results together.
 */
public class RepeatedInteractionDomain<S, T> implements InteractionDomain<S, T> {

	private final InteractionDomain<S, T> domain;
	private final int repeats;

	@AccessedViaReflection
	public RepeatedInteractionDomain(InteractionDomain<S, T> domain, int repeats) {
		Preconditions.checkArgument(0 < repeats);
		this.domain = domain;
		this.repeats = repeats;
	}

	@Override
	public InteractionResult interact(S candidate, T opponent, RandomDataGenerator random) {
		InteractionResult summedResult = new InteractionResult();
		for (int i = 0; i < repeats; ++i) {
			InteractionResult result = domain.interact(candidate, opponent, random);
			summedResult = summedResult.add(result);
		}
		return summedResult.divide(repeats);
	}
}
