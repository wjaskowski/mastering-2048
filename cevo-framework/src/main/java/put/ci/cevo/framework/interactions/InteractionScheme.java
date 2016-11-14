package put.ci.cevo.framework.interactions;

import put.ci.cevo.util.random.ThreadedContext;

import java.util.List;

public interface InteractionScheme<S, T> {

	public InteractionTable<S, T> interact(List<S> solutions, List<T> tests, ThreadedContext context);
}
