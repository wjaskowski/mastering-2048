package put.ci.cevo.experiments.profiles.generators;

import put.ci.cevo.util.random.ThreadedContext;

public interface StrategyGenerator<T> {

	public T createNext(ThreadedContext context);

	public void reset();

}
