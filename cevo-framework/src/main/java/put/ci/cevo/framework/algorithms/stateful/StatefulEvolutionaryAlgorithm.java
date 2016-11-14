package put.ci.cevo.framework.algorithms.stateful;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.util.random.ThreadedContext;

import java.util.List;

public interface StatefulEvolutionaryAlgorithm<T> {
	
	public void nextEvolutionStep(ThreadedContext context);

	public void initializePopulation(RandomDataGenerator random);

	public List<T> getCurrentPopulation();

}
