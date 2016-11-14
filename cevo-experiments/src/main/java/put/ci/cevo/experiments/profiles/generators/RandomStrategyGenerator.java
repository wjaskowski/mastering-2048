package put.ci.cevo.experiments.profiles.generators;

import put.ci.cevo.framework.factories.IndividualFactory;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.annotations.AccessedViaReflection;

public class RandomStrategyGenerator<T> implements StrategyGenerator<T> {

	private final IndividualFactory<T> individualsFactory;

	@AccessedViaReflection
	public RandomStrategyGenerator(IndividualFactory<T> individualsFactory) {
		this.individualsFactory = individualsFactory;
	}

	@Override
	public T createNext(ThreadedContext context) {
		return individualsFactory.createRandomIndividual(context.getRandomForThread());
	}

	@Override
	public void reset() {
		// Nothing
	}
}
