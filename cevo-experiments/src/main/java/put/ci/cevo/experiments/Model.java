package put.ci.cevo.experiments;

import put.ci.cevo.framework.algorithms.GenerationalOptimizationAlgorithm;
import put.ci.cevo.framework.algorithms.history.EvolutionHistory;
import put.ci.cevo.framework.algorithms.history.MemoryEvolutionHistory;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.random.ThreadedRandom;
import put.ci.cevo.framework.retrospection.EvolutionaryRetrospector;
import put.ci.cevo.framework.retrospection.Retrospector;
import put.ci.cevo.framework.state.listeners.EvolutionStateListener;
import put.ci.cevo.framework.state.listeners.ProgressInfoListener;
import put.ci.cevo.util.stats.EventsLogger;
import put.ci.cevo.util.stats.EventsLoggerFactory;
import put.ci.cevo.framework.termination.EvolutionTarget;
import put.ci.cevo.util.annotations.AccessedViaReflection;

import static com.google.common.base.Objects.toStringHelper;

public class Model {

	private final String name;
	private final EvolutionHistory history;
	private final GenerationalOptimizationAlgorithm algorithm;
	private final EventsLogger logger;

	@AccessedViaReflection
	public Model(GenerationalOptimizationAlgorithm algorithm, String name) {
		this(algorithm, new MemoryEvolutionHistory(), name);
	}

	@AccessedViaReflection
	public Model(GenerationalOptimizationAlgorithm algorithm, EvolutionHistory history, String name) {
		this(algorithm, history, new ProgressInfoListener(), name);
	}

	@AccessedViaReflection
	public Model(GenerationalOptimizationAlgorithm algorithm, EvolutionHistory history,
			EvolutionStateListener listener, String name) {
		this.algorithm = algorithm;
		this.history = history;
		this.name = name;
		this.logger = EventsLoggerFactory.createDefault();
		addListener(listener);
	}

	public Retrospector evolve(EvolutionTarget target, ThreadedRandom random, int threads) {
		algorithm.addNextGenerationListener(history);
		algorithm.evolve(target, ThreadedContext.withEventsLogger(random, threads, logger));
		return new EvolutionaryRetrospector(history);
	}

	public EvolutionHistory getHistory() {
		return history;
	}

	public GenerationalOptimizationAlgorithm getAlgorithm() {
		return algorithm;
	}

	public String getName() {
		return name;
	}

	public EventsLogger getEventsLogger() {
		return logger;
	}
	
	public void addListener(EvolutionStateListener listener) {
		algorithm.addNextGenerationListener(listener);
	}

	@Override
	public String toString() {
		return toStringHelper(this).add("name", name).toString();
	}

}
