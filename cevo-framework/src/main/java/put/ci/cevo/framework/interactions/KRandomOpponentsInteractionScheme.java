package put.ci.cevo.framework.interactions;

import com.google.common.base.Preconditions;
import put.ci.cevo.util.RandomUtils;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.random.ThreadedContext;

import java.util.List;

public final class KRandomOpponentsInteractionScheme<S, T> implements InteractionScheme<S, T> {

	private final int numRandomOpponents;
	private final RoundRobinInteractionScheme<S, T> roundRobin;

	@AccessedViaReflection
	public KRandomOpponentsInteractionScheme(InteractionDomain<S, T> domain, int numRandomOpponents) {
		Preconditions.checkArgument(numRandomOpponents > 0);
		roundRobin = new RoundRobinInteractionScheme<>(domain);
		this.numRandomOpponents = numRandomOpponents;
	}

	@Override
	public InteractionTable<S, T> interact(List<S> solutions, List<T> tests, ThreadedContext context) {
		tests = RandomUtils.sample(tests, numRandomOpponents, context.getRandomForThread());
		return roundRobin.interact(solutions, tests, context);
	}
}
