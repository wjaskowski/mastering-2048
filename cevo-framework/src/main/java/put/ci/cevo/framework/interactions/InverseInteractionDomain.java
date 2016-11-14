package put.ci.cevo.framework.interactions;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.util.annotations.AccessedViaReflection;

public class InverseInteractionDomain<S, T> implements InteractionDomain<S, T> {

	private final InteractionDomain<T, S> originalInteractionDomain;

	@AccessedViaReflection
	public InverseInteractionDomain(InteractionDomain<T, S> originalInteractionDomain) {
		this.originalInteractionDomain = originalInteractionDomain;
	}

	@Override
	public InteractionResult interact(S candidate, T opponent, RandomDataGenerator random) {
		return originalInteractionDomain.interact(opponent, candidate, random).inverted();
	}
}
