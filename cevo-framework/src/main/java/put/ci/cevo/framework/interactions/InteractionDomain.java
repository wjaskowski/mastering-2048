package put.ci.cevo.framework.interactions;

import org.apache.commons.math3.random.RandomDataGenerator;

public interface InteractionDomain<S, T> {

	public InteractionResult interact(S candidate, T opponent, RandomDataGenerator random);

}
