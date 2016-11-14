package put.ci.cevo.framework.operators;

import org.apache.commons.math3.random.RandomDataGenerator;

public interface EvolutionaryOperator<S, T> {

	public T produce(final S individual, final RandomDataGenerator random);

}
